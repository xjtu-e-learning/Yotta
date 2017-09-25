package Yotta.spider.service.topic;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Domain;
import Yotta.spider.domain.Topic;
import Yotta.spider.domain.TopicComplex;
import Yotta.spider.repository.DomainRepository;
import Yotta.spider.repository.TopicRelationRepository;
import Yotta.spider.repository.TopicRepository;
import Yotta.spider.service.utils.DownloaderService;
import Yotta.spider.service.utils.ExtractTopicService;
import Yotta.utils.ResultUtil;
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

/**
 * 取中文维基的主题及其上下位关系：主题及其上下位关系的 “爬取、增、删、改、查”
 * Created by yuanhao on 2017/9/25.
 */
@Service
public class TopicService {

    @Autowired
    public TopicService() { // controller自动注入使用
    }

    // 打印信息
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // 自己的服务
    @Autowired
    private SpiderTopicService spiderTopicService;

    // 数据库相关操作
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TopicRelationRepository topicRelationRepository;


    /**
     * 插入主题
     * @param topic 插入主题
     * @return 插入结果
     */
    public Result insertTopic(Topic topic) {
        // 插入主题名不能为空
        if (topic.getTopicName() == null || "".equals(topic.getTopicName()) || topic.getTopicName().length() == 0) {
            return ResultUtil.error(ResultEnum.TOPICINSERTNOTNULL_ERROR.getCode(), ResultEnum.TOPICINSERTNOTNULL_ERROR.getMsg());
        }
        // 插入主题不存在
        if (topicRepository.findByDomainIdAndTopicName(topic.getDomainId(), topic.getTopicName()) == null) {
            Topic topic1 = topicRepository.save(topic);
            if (topic1 != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topic1);
            } else {
                return ResultUtil.error(ResultEnum.TOPICINSERT_ERROR.getCode(), ResultEnum.TOPICINSERT_ERROR.getMsg());
            }
        }
        // 插入主题已存在
        return ResultUtil.error(ResultEnum.TOPICINSERTDUPLICATE_ERROR.getCode(), ResultEnum.TOPICINSERTDUPLICATE_ERROR.getMsg());
    }

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
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询领域失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param topicPage 分页查询结果
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
     * 根据 领域Id 得到所有主题
     * @param domainId 领域主题ID
     * @return 查询结果
     */
    public Result getTopicByDomainId(Long domainId) {
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicRepository.findByDomainId(domainId));
    }

    /**
     * 根据 领域Id 判断主题数据是否爬取
     * @param domainId 领域主题ID
     * @return 判断结果
     */
    public Result judgeTopicByDomainId(Long domainId) {
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
     * @return 爬取结果
     * @throws Exception
     */
    public Result storeByDomainName(String domainName) throws Exception{
        if (spiderTopicService.hasCategoryPage(domainName)) { // 该领域存在目录页面，可以爬取主题信息
            Domain domain = domainRepository.findByDomainName(domainName);
            if (domain == null) { // 领域表不存在则添加
                Domain domainAdd = new Domain();
                domainAdd.setDomainName(domainName);
                domainAdd.setDomainId(new Long(1)); // 中文维基百科
                domainRepository.save(domainAdd);
            }
            Long domainID = domain.getDomainId();
            if (topicRepository.findByDomainId(domainID).size() == 0) { // 判断是否已经爬取过该领域的主题信息
                TopicComplex topicComplex = spiderTopicService.getTopicAndRelation(domain);
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
                TopicComplex topicComplex = spiderTopicService.getTopicAndRelation(domain);
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), topicComplex);
            } else {
                logger.error("领域ID为：" + domainId + "，领域名为：" + domain.getDomainName() + "  的主题及其主题关系的数据已经爬取，无需重复爬取");
                return ResultUtil.error(ResultEnum.DUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.DUPLICATEDCRAWLER_ERROR.getMsg());
            }
        }
    }

}
