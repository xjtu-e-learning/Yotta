package Yotta.spider.service.Facet;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Topic;
import Yotta.spider.domain.TopicRelation;
import Yotta.spider.repository.DomainRepository;
import Yotta.spider.repository.FacetRepository;
import Yotta.spider.repository.TopicRelationRepository;
import Yotta.spider.repository.TopicRepository;
import Yotta.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by yuanhao on 2017/9/27.
 */
@Service
public class FacetService {

    @Autowired
    public FacetService() {

    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SpiderFacetService spiderFacetService;

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TopicRelationRepository topicRelationRepository;
    @Autowired
    private FacetRepository facetRepository;

    /**
     * 根据主题，返回该主题的子主题数、父主题数、分面数
     * @param topicId 主题Id
     * @return 查询结果
     */
    public Result judgeFacetByTopicId(Long topicId) {
        List<String> results = new ArrayList<>(); // 保存结果：子主题数，父主题数，分面数
        List<TopicRelation> childTopics = topicRelationRepository.findByParentTopicId(topicId); // 找到所有子主题
        List<TopicRelation> parentTopics = topicRelationRepository.findByChildTopicId(topicId); // 找到所有父主题.
        String facetInfo = "";
        results.add(childTopics.size() + "");
        results.add(parentTopics.size() + "");
        if (facetRepository.findByTopicId(topicId).size() != 0) {
            facetInfo = "分面已爬取，总数为：" + facetRepository.findByTopicId(topicId).size()
                    + "，一级分面数为：" + facetRepository.findByTopicIdAndFacetLayer(topicId, 1L).size()
                    + "，二级分面数为：" + facetRepository.findByTopicIdAndFacetLayer(topicId, 2L).size()
                    + "，三级分面数为：" + facetRepository.findByTopicIdAndFacetLayer(topicId, 3L).size();
            results.add(facetInfo);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
        } else {
            results.add(facetInfo);
            return ResultUtil.error(ResultEnum.FACETQUERY_ERROR.getCode(), ResultEnum.FACETQUERY_ERROR.getMsg(), results);
        }
    }

    /**
     * 根据主题，爬取主题的分面和碎片信息
     * @param topic 主题
     * @return 爬取结果
     * @throws Exception
     */
    public Result storeAllFacetAndContentByTopic(Topic topic) throws Exception {
        if (facetRepository.findByTopicId(topic.getTopicId()).size() == 0) { // 判断是否已经爬取过该领域的主题信息
            List<Object> list = spiderFacetService.storeFacetAndRelationByTopic(topic);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), list);
        } else {
            logger.error("主题Id为：" + topic.getTopicId() + "，主题名为：" + topic.getTopicName() + "  的分面和碎片数据已经爬取，无需重复爬取");
            return ResultUtil.error(ResultEnum.FACETDUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.FACETDUPLICATEDCRAWLER_ERROR.getMsg());
        }
    }


}
