package Yotta.spider.service.topic;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.*;
import Yotta.spider.repository.DomainRepository;
import Yotta.spider.repository.TopicRelationRepository;
import Yotta.spider.repository.TopicRepository;
import Yotta.spider.service.utils.DownloaderService;
import Yotta.spider.service.utils.ExtractTopicService;
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
 * 爬取中文维基的主题及其上下位关系：解析获取主题及其上下位关系
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
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            logger.info("sleep for seconds for inserting data...");
        }
        complementTopicRelation();
        return topicComplexNew;
    }

    /**
     * 补全主题关系表中所有关系中父主题和子主题的id信息
     * 注意：父主题可能为领域名，此时没有对应的主题id
     */
    public void complementTopicRelation() {
        List<TopicRelation> relationList = topicRelationRepository.findAll();
        for (int i = 0; i < relationList.size(); i++) {
            TopicRelation topicRelation = relationList.get(i);
            Topic childTopic = topicRepository.findByDomainIdAndTopicNameAndTopicLayer(topicRelation.getDomainId(), topicRelation.getChildTopicName(), topicRelation.getChildTopicLayer());
            Topic parentTopic = topicRepository.findByDomainIdAndTopicNameAndTopicLayer(topicRelation.getDomainId(), topicRelation.getParentTopicName(), topicRelation.getParentTopicLayer());
            if (parentTopic != null) {
                topicRelationRepository.updateByRelationId(topicRelation.getRelationId(), childTopic.getTopicId(), parentTopic.getTopicId());
            } else {
                topicRelationRepository.updateByRelationId(topicRelation.getRelationId(), childTopic.getTopicId()); // 父主题为领域名，此时不用更新父节点
            }
        }
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
