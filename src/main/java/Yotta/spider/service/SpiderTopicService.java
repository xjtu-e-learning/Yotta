package Yotta.spider.service;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.*;
import Yotta.spider.repository.DomainRepository;
import Yotta.spider.repository.TopicRelationRepository;
import Yotta.spider.repository.TopicRepository;
import Yotta.utils.ResultUtil;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.net.URLEncoder;
import java.util.*;

/**
 * 爬取中文维基的主题及其上下位关系
 * Created by yuanhao on 2017/4/28.
 */
@Service
public class SpiderTopicService {

    @Autowired
    public SpiderTopicService() { // controller自动注入使用
    }

    // 打印信息
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // 自己的服务
    @Autowired
    private ExtractTopicService extractTopicService;
    @Autowired
    private DownloaderService downloaderService;

    // 数据库相关操作
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TopicRelationRepository topicRelationRepository;

    /**
     * 获取分页主题数据，按照主题ID排序 (带查询条件：根据同一领域Id下的数据)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @param domainId 领域Id (查询条件)
     * @return 分页排序的数据
     */
    public Result getTopicByDomainIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long domainId) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "domainId");
        // lamada表达式的写法
        Page<Topic> topicPage = topicRepository.findAll(
                (Root<Topic> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("domainId").as(Long.class), domainId);
                }, pageable);
        return topicPageJudge(topicPage);
//        Page<Domain> domainPage = domainRepository.findAll(new Specification<Domain>(){
//            @Override
//            public Predicate toPredicate(Root<Domain> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                // 多个条件查询
////                Predicate p1 = criteriaBuilder.equal(root.get("domainId").as(Long.class), domainId);
////                Predicate p2 = criteriaBuilder.equal(root.get("domainName").as(String.class), domainName);
////                Predicate p3 = criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
////                query.where(criteriaBuilder.and(p1,p2,p3));
////                return query.getRestriction();
//                return criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
//            }
//        }, pageable);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询领域失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param domainPage 分页查询结果
     * @return 返回查询结果
     */
    public Result topicPageJudge(Page<Topic> topicPage) {
        if (topicPage.getTotalElements() == 0) { // 没有查到数据
            return ResultUtil.error(ResultEnum.TOPICQUERY_ERROR.getCode(), ResultEnum.TOPICQUERY_ERROR.getMsg());
        } else if (topicPage.getNumber() >= topicPage.getTotalPages()) { // 查询的页数超过最大页数
            return ResultUtil.error(ResultEnum.PAGE_ERROR.getCode(), ResultEnum.PAGE_ERROR.getMsg());
        }
        // 返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicPage);
    }

    /**
     * 根据 领域Id 判断主题数据是否爬取
     * @param domainId 领域主题ID
     * @return
     */
    public Result getTopicByDomainId(Long domainId) {
        if (topicRepository.findByDomainId(domainId).size() != 0) { // 判断是否已经爬取过该领域的主题信息
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicRepository.findByDomainId(domainId).size());
        } else {
            logger.info("领域ID为：" + domainId + "，对应的主题信息没有爬取");
            return ResultUtil.error(ResultEnum.DOMAINTOPICNOTSPIDER_ERROR.getCode(), ResultEnum.DOMAINTOPICNOTSPIDER_ERROR.getMsg());
        }
    }

    /**
     * 根据领域名爬取主题信息
     * @param domainName 领域名
     * @return
     * @throws Exception
     */
    public Result storeByDomainName(String domainName) throws Exception{
        if (this.hasCategoryPage(domainName)) { // 该领域存在目录页面，可以爬取主题信息
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain == null) { // 领域表不存在则添加
                Domain domainAdd = new Domain();
                domainAdd.setDomainName(domainName);
                domainAdd.setDomainId(new Long(1)); // 中文维基百科
                domainRepository.save(domainAdd);
            }
            Long domainID = domain.getDomainId();
            if (topicRepository.findByDomainId(domainID).size() == 0) { // 判断是否已经爬取过该领域的主题信息
                TopicComplex topicComplex = getTopicAndRelation(domain);
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicComplex);
            } else {
                logger.error("领域ID为：" + domainID + "，领域名为：" + domain.getDomainName() + "  的主题及其主题关系的数据已经爬取，无需重复爬取");
                return ResultUtil.error(ResultEnum.DUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.DUPLICATEDCRAWLER_ERROR.getMsg());
            }
        } else {
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain != null) {  // 领域表存在则删除
                domainRepository.delete(domain);
            }
            logger.error("领域名为：" + domainName + " 对应的中文维基目录页面不存在，请核对领域名");
            return ResultUtil.error(ResultEnum.EMPTYDOMAINNAME_ERROR.getCode(), ResultEnum.EMPTYDOMAINNAME_ERROR.getMsg());
        }
    }

    /**
     * 根据领域ID爬取主题信息
     * @Param domainId 领域ID
     * @return 爬取结果
     * @throws Exception
     */
    public Result storeByDomainID(Long domainId) throws Exception{
        Domain domain = domainRepository.findByDomainId(domainId);
        if (domain == null) {
            logger.error("领域ID为：" + domainId + " 对应的领域不存在，请核对数据");
            return ResultUtil.error(ResultEnum.DOMAINID_ERROR.getCode(), ResultEnum.DOMAINID_ERROR.getMsg());
        } else {
            if (topicRepository.findByDomainId(domainId).size() == 0) { // 判断是否已经爬取过该领域的主题信息
                TopicComplex topicComplex = getTopicAndRelation(domain);
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicComplex);
            } else {
                logger.error("领域ID为：" + domainId + "，领域名为：" + domain.getDomainName() + "  的主题及其主题关系的数据已经爬取，无需重复爬取");
                return ResultUtil.error(ResultEnum.DUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.DUPLICATEDCRAWLER_ERROR.getMsg());
            }
        }
    }


    /**
     * 获取三层主题及其上下位关系：单个领域
     * @throws Exception
     * @Return 返回三层主题及其上下位关系
     */
    public TopicComplex getTopicAndRelation(Domain domain) throws Exception{
        Long classID = domain.getDomainId();
        String className = domain.getDomainName();

        // 第一层
        String domain_url = "https://zh.wikipedia.org/wiki/Category:" + URLEncoder.encode(className ,"UTF-8");
        Long layerID = 1L;
        List<Topic> topicFirstNotChild = this.topicNotChild(domain_url); // 不包含子主题的第一层主题
        List<Topic> topicFirstHasChild = this.topicHasChild(domain_url); // 包含子主题的第一层主题
        List<Topic> topicFirst1 = this.topicConvert(topicFirstNotChild, layerID, classID);
        List<Topic> topicFirst2 = this.topicConvert(topicFirstHasChild, layerID, classID);
        List<Topic> topicFirst = topicFirst1;
        topicFirst.addAll(topicFirst2); // 保存所有第一层主题
//        topicRepository.save(topicFirst1);
//        topicRepository.save(topicFirst2);
        // 主题间的上下位关系，第0层（领域名）和第一层主题
        List<TopicRelation> topicRelationList0 = new ArrayList<>();
        for (int i = 0; i < topicFirst.size(); i++) {
            Topic topic = topicFirst.get(i);
            String topicName = topic.getTopicName();
            Long topicLayer = 1L;
            topicRelationList0.add(new TopicRelation(className, 0L, topicName, 1L, classID));
        }


        // 第二层
        layerID = 2L;
        List<Topic> topicSecondNotChild = new ArrayList<>(); // 不包含子主题的第二层主题
        List<Topic> topicSecondHasChild = new ArrayList<>(); // 包含子主题的第二层主题
        List<TopicRelation> topicRelationList = new ArrayList<>(); // 主题间的上下位关系，第一层和第二层主题
        List<Topic> topicSecond = new ArrayList<>(); // 保存所有第二层主题
        if(topicFirstHasChild.size() != 0){
            for(int i = 0; i < topicFirstHasChild.size(); i++){
                Topic topic = topicFirstHasChild.get(i);
                String url = topic.getTopicUrl();
                // 获得每个主题的子主题集合
                List<Topic> topicSecondList1 = this.topicNotChild(url);
                topicSecondNotChild.addAll(topicSecondList1);
                List<Topic> topicSecondList2 = this.topicHasChild(url);
                topicSecondHasChild.addAll(topicSecondList2);
                // 获得每个主题及其子主题的上下位关系
                if (topicSecondList1.size() > 0){
                    for (int j = 0; j < topicSecondList1.size(); j++) {
                        Topic topic1 = topicSecondList1.get(j);
                        String parentTopicName = topic.getTopicName(); // 父主题
                        Long parentTopicLayer = 1L;
                        String childTopicName = topic1.getTopicName(); // 子主题
                        Long childTopicLayer = 2L;
                        topicRelationList.add(new TopicRelation(parentTopicName, parentTopicLayer, childTopicName, childTopicLayer, classID));
                    }
                }
                if (topicSecondList2.size() > 0){
                    for (int j = 0; j < topicSecondList2.size(); j++) {
                        Topic topic1 = topicSecondList2.get(j);
                        String parentTopicName = topic.getTopicName(); // 父主题
                        Long parentTopicLayer = 1L;
                        String childTopicName = topic1.getTopicName(); // 子主题
                        Long childTopicLayer = 2L;
                        topicRelationList.add(new TopicRelation(parentTopicName, parentTopicLayer, childTopicName, childTopicLayer, classID));
                    }
                }
            }
            // 存储主题及其上下位关系信息
            List<Topic> topicSecond1 = this.topicConvert(topicSecondNotChild, layerID, classID);
            List<Topic> topicSecond2 = this.topicConvert(topicSecondHasChild, layerID, classID);
            topicSecond = topicSecond1;
            topicSecond.addAll(topicSecond2); // 保存所有第二层主题
//            topicRepository.save(topicSecond1);
//            topicRepository.save(topicSecond2);
//            topicRelationRepository.save(topicRelationList);
        } else {
            logger.info("不存在第一层主题含有第二层主题...");
        }

        // 第三层
        layerID = 3L;
        List<Topic> topicThirdNotChild = new ArrayList<>(); // 不包含子主题的第三层主题
        List<Topic> topicThirdHasChild = new ArrayList<>(); // 包含子主题的第三层主题
        List<TopicRelation> topicRelationList2 = new ArrayList<>(); // 主题间的上下位关系，第二层和第三层主题
        List<Topic> topicThird = new ArrayList<>(); // 保存所有第三层主题
        if(topicSecondHasChild.size() != 0){
            for(int i = 0; i < topicSecondHasChild.size(); i++){
                Topic topic = topicSecondHasChild.get(i);
                String url = topic.getTopicUrl();
                // 获得每个主题的子主题集合
                List<Topic> topicThirdList1 = this.topicNotChild(url);
                topicThirdNotChild.addAll(topicThirdList1);
                List<Topic> topicThirdList2 = this.topicHasChild(url);
                topicThirdHasChild.addAll(topicThirdList2);
                // 获得每个主题及其子主题的上下位关系
                if (topicThirdList1.size() > 0){
                    for (int j = 0; j < topicThirdList1.size(); j++) {
                        Topic topic1 = topicThirdList1.get(j);
                        String parentTopicName = topic.getTopicName(); // 父主题
                        Long parentTopicLayer = 2L;
                        String childTopicName = topic1.getTopicName(); // 子主题
                        Long childTopicLayer = 3L;
                        topicRelationList2.add(new TopicRelation(parentTopicName, parentTopicLayer, childTopicName, childTopicLayer, classID));
                    }
                }
                if (topicThirdList2.size() > 0){
                    for (int j = 0; j < topicThirdList2.size(); j++) {
                        Topic topic1 = topicThirdList2.get(j);
                        String parentTopicName = topic.getTopicName(); // 父主题
                        Long parentTopicLayer = 2L;
                        String childTopicName = topic1.getTopicName(); // 子主题
                        Long childTopicLayer = 3L;
                        topicRelationList2.add(new TopicRelation(parentTopicName, parentTopicLayer, childTopicName, childTopicLayer, classID));
                    }
                }
            }
            // 存储主题及其上下位关系信息
            List<Topic> topicThird1 = this.topicConvert(topicThirdNotChild, layerID, classID);
            List<Topic> topicThird2 = this.topicConvert(topicThirdHasChild, layerID, classID);
            topicThird = topicThird1;
            topicThird.addAll(topicThird2);  // 保存所有第三层主题
//            topicRepository.save(topicThird1);
//            topicRepository.save(topicThird2);
//            topicRelationRepository.save(topicRelationList2);
        } else {
            logger.info("不存在第二层主题含有第三层主题...");
        }

        // 该领域三层主题的信息
        TopicComplex topicComplex = new TopicComplex();
        topicComplex.setTopicFirst(topicFirst);
        topicComplex.setTopicSecond(topicSecond);
        topicComplex.setTopicThird(topicThird);
        topicComplex.setClassToFirst(topicRelationList0);
        topicComplex.setFirstToSecond(topicRelationList);
        topicComplex.setSecondToThird(topicRelationList2);

        TopicComplex topicComplexNew = this.getTopicComplex(topicComplex);
        topicRepository.save(topicComplexNew.getTopicFirst());
        topicRepository.save(topicComplexNew.getTopicSecond());
        topicRepository.save(topicComplexNew.getTopicThird());
        topicRelationRepository.save(topicComplexNew.getClassToFirst());
        topicRelationRepository.save(topicComplexNew.getFirstToSecond());
        topicRelationRepository.save(topicComplexNew.getSecondToThird());
        return topicComplexNew;
    }

    /**
     * 获取Category中的页面术语
     * @param url 主题目录链接
     * @return 没有子主题的主题集合
     * @throws Exception
     */
    public List<Topic> topicNotChild(String url) throws Exception{
        String html = downloaderService.seleniumWikiCNIE(url); // Selenium方式获取
//		String html = downloaderService.httpClientWikiCN(url); // HttpClient方式获取
//		String html = downloaderService.webmagicWikiCN(url); // Webmagic下载中间件方式获取
        Document doc = downloaderService.parseHtmlText(html);
        List<Topic> termList = extractTopicService.getTopicNotChild(doc); // 解析没有子分类的术语
        return termList;
    }

    /**
     * 获取Category中的子分类术语
     * @param url 主题目录链接
     * @return 含有子主题的主题集合
     * @throws Exception
     */
    public List<Topic> topicHasChild(String url) throws Exception{
        String html = downloaderService.seleniumWikiCNIE(url); // Selenium方式获取
//		String html = downloaderService.httpClientWikiCN(url); // HttpClient方式获取
//		String html = downloaderService.webmagicWikiCN(url); // Webmagic下载中间件方式获取
        Document doc = downloaderService.parseHtmlText(html);
        List<Topic> termList = extractTopicService.getTopicHasChild(doc); // 解析有子分类的术语
        return termList;
    }


    /**
     * 补全topic的主题信息
     * @param topics
     * @param topicLayer
     * @param classID
     * @return
     */
    public List<Topic> topicConvert(List<Topic> topics, Long topicLayer, Long classID) {
        for (Topic topic : topics) {
            topic.setTopicLayer(topicLayer);
            topic.setDomainId(classID);
        }
        return topics;
    }


    /**
     * 领域术语抽取：获取知识主题
     * 判断三层节点中存在的重复元素并进行处理，得到知识主题（测试程序）
     * @param topicFirst
     * @param topicSecond
     * @param topicThird
     */
    public TopicComplex getTopicComplex(TopicComplex topicComplex){
        // 处理后的集合
        List<Topic> topicFirstFinal = new ArrayList<>();
        List<Topic> topicSecondFinal = new ArrayList<>();
        List<Topic> topicThirdFinal = new ArrayList<>();
        List<TopicRelation> classToFirstFinal = new ArrayList<>();
        List<TopicRelation> firstToSecondFinal = new ArrayList<>();
        List<TopicRelation> secondToThirdFinal = new ArrayList<>();
        // 去除重复元素的三层主题集合
        List<Topic> topicFirst = this.deleteDuplicateTopic(topicComplex.getTopicFirst());
        List<Topic> topicSecond = this.deleteDuplicateTopic(topicComplex.getTopicSecond());
        List<Topic> topicThird = this.deleteDuplicateTopic(topicComplex.getTopicThird());
        // 去除重复元素的主题关系集合
        List<TopicRelation> classToFirst = this.deleteDuplicateRelation(topicComplex.getClassToFirst());
        List<TopicRelation> firstToSecond = this.deleteDuplicateRelation(topicComplex.getFirstToSecond());
        List<TopicRelation> secondToThird = this.deleteDuplicateRelation(topicComplex.getSecondToThird());

        // 第一层元素不在第二层和第三层中可以保存
        for(Topic term : topicFirst){
            String termName = term.getTopicName().trim();
            Boolean flag = false; // 标志位判断第一层领域术语是否在第三层中
            for (Topic term2 : topicSecond) {
                if(termName.equals(term2.getTopicName().trim())){
                    flag = true;
                }
            }
            for (Topic term3 : topicThird) {
                if(termName.equals(term3.getTopicName().trim())){
                    flag = true;
                }
            }
            if(!flag){
                topicFirstFinal.add(term);
            }
        }

        // 第二层元素不在第三层中可以保存
        for(Topic term2 : topicSecond){
            String termName = term2.getTopicName().trim();
            Boolean flag = true; // 标志位判断第二层领域术语是否在第三层中
            for (Topic term3 : topicThird) {
                if(termName.equals(term3.getTopicName().trim())){
                    flag = false;
                }
            }
            if(flag){
                topicSecondFinal.add(term2);
            }
        }

        // 第三层元素的始终不变
        topicThirdFinal = topicThird;

        // 去除不对的关系
        // 判断关系中的第一层主题是否在相应的主题集合中
        for (TopicRelation topicRelation : classToFirst) {
            boolean flag1 = false;
            String childTopicName = topicRelation.getChildTopicName();
            Long childTopicLayer = topicRelation.getChildTopicLayer();
            for (int i = 0; i < topicFirstFinal.size(); i++) {
                Topic topic = topicFirstFinal.get(i);
                String topicName = topic.getTopicName();
                Long topicLayer = topic.getTopicLayer();
                if (topicName.equals(childTopicName) && topicLayer.equals(childTopicLayer)) {
                    flag1 = true;
                    break;
                }
            }
            if (flag1) {
                classToFirstFinal.add(topicRelation);
            }
        }
        // 判断关系中的第一层主题和第二层主题是否都在相应的主题集合中
        for (TopicRelation topicRelation : firstToSecond) {
            boolean flag1 = false;
            boolean flag2 = false;
            String parentTopicName = topicRelation.getParentTopicName();
            Long parentTopicLayer = topicRelation.getParentTopicLayer();
            String childTopicName = topicRelation.getChildTopicName();
            Long childTopicLayer = topicRelation.getChildTopicLayer();
            for (int i = 0; i < topicFirstFinal.size(); i++) {
                Topic topic = topicFirstFinal.get(i);
                String topicName = topic.getTopicName();
                Long topicLayer = topic.getTopicLayer();
                if (topicName.equals(parentTopicName) && topicLayer.equals(parentTopicLayer)) {
                    flag1 = true;
                    break;
                }
            }
            for (int i = 0; i < topicSecondFinal.size(); i++) {
                Topic topic = topicSecondFinal.get(i);
                String topicName = topic.getTopicName();
                Long topicLayer = topic.getTopicLayer();
                if (topicName.equals(childTopicName) && topicLayer.equals(childTopicLayer)) {
                    flag2 = true;
                    break;
                }
            }
            if (flag1 && flag2) {
                firstToSecondFinal.add(topicRelation);
            }
        }
        // 判断关系中的第二层主题和第三层主题是否都在相应的主题集合中
        for (TopicRelation topicRelation : secondToThird) {
            boolean flag1 = false;
            boolean flag2 = false;
            String parentTopicName = topicRelation.getParentTopicName();
            Long parentTopicLayer = topicRelation.getParentTopicLayer();
            String childTopicName = topicRelation.getChildTopicName();
            Long childTopicLayer = topicRelation.getChildTopicLayer();
            for (int i = 0; i < topicSecondFinal.size(); i++) {
                Topic topic = topicSecondFinal.get(i);
                String topicName = topic.getTopicName();
                Long topicLayer = topic.getTopicLayer();
                if (topicName.equals(parentTopicName) && topicLayer.equals(parentTopicLayer)) {
                    flag1 = true;
                    break;
                }
            }
            for (int i = 0; i < topicThirdFinal.size(); i++) {
                Topic topic = topicThirdFinal.get(i);
                String topicName = topic.getTopicName();
                Long topicLayer = topic.getTopicLayer();
                if (topicName.equals(childTopicName) && topicLayer.equals(childTopicLayer)) {
                    flag2 = true;
                    break;
                }
            }
            if (flag1 && flag2) {
                secondToThirdFinal.add(topicRelation);
            }
        }

        TopicComplex result = new TopicComplex(topicFirstFinal, topicSecondFinal, topicThirdFinal, classToFirstFinal,firstToSecondFinal, secondToThirdFinal);
        return result;
    }

    /**
     * 去除重复的主题Topic
     * 时间复杂度为 O(n^2)，可以优化
     * @param topicList
     * @return
     */
    public List<Topic> deleteDuplicateTopic(List<Topic> topicList){
        List<Topic> result = new ArrayList<>(); // 没有重复元素的List
        for (int i = 0; i < topicList.size(); i++) {
            Topic topic = topicList.get(i);
            int count = 1; // 判断出现次数
            String topicName = topic.getTopicName();
            String topicUrl = topic.getTopicUrl();
            for (int j = i + 1; j < topicList.size(); j++) { // 遍历后面的元素
                Topic topic1 = topicList.get(j);
                if (topicName.equals(topic1.getTopicName()) && topicUrl.equals(topic1.getTopicUrl())) {
                    count++;
                }
            }
            if (count == 1) { // 只出现一次的元素
                result.add(topic);
            }
        }
        return result;
    }

    /**
     * 去除重复的主题间关系TopicRelation
     * 时间复杂度为 O(n^2)，可以优化
     * @param topicRelationList
     * @return
     */
    public List<TopicRelation> deleteDuplicateRelation(List<TopicRelation> topicRelationList){
        List<TopicRelation> result = new ArrayList<>(); // 没有重复元素的List
        for (int i = 0; i < topicRelationList.size(); i++) {
            TopicRelation topicRelation = topicRelationList.get(i);
            int count = 1; // 判断出现次数
            String parentTopicName = topicRelation.getParentTopicName();
            Long parentTopicLayer = topicRelation.getParentTopicLayer();
            String childTopicName = topicRelation.getChildTopicName();
            Long childTopicLayer = topicRelation.getChildTopicLayer();
            for (int j = i + 1; j < topicRelationList.size(); j++) { // 遍历后面的元素
                TopicRelation topicRelation1 = topicRelationList.get(j);
                if (parentTopicName.equals(topicRelation1.getParentTopicName()) &&
                        parentTopicLayer.equals(topicRelation1.getParentTopicLayer()) &&
                        childTopicName.equals(topicRelation1.getChildTopicName()) &&
                        childTopicLayer.equals(topicRelation1.getChildTopicLayer())) {
                    count++;
                }
            }
            if (count == 1) { // 只出现一次的元素
                result.add(topicRelation);
            }
        }
        return result;
    }

    /**
     * 判断某个词条是否存在主题页面
     * @param url
     * @return
     * @throws Exception
     */
    public boolean hasCategoryPage(String domainName) throws Exception{
        String domain_url = "https://zh.wikipedia.org/wiki/Category:" + URLEncoder.encode(domainName ,"UTF-8");
        String html = downloaderService.seleniumWikiCNIE(domain_url); // Selenium方式获取
        Document doc = downloaderService.parseHtmlText(html);
        return extractTopicService.getCategoryPage(doc);
    }

}
