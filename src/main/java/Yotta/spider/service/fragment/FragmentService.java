package Yotta.spider.service.fragment;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.FacetRelation;
import Yotta.spider.domain.Topic;
import Yotta.spider.domain.TopicRelation;
import Yotta.spider.repository.*;
import Yotta.spider.service.Facet.SpiderFacetService;
import Yotta.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 碎片
 * Created by yuanhao on 2017/9/27.
 */
@Service
public class FragmentService {

    @Autowired
    public FragmentService() {

    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SpiderFragmentService spiderFragmentService;

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private TopicRelationRepository topicRelationRepository;
    @Autowired
    private FacetRepository facetRepository;
    @Autowired
    private FacetRelationRepository facetRelationRepository;
    @Autowired
    private AssembleTextRepository assembleTextRepository;
    @Autowired
    private AssembleImageRepository assembleImageRepository;


    /**
     * 根据分面，返回该分面的子分面数、父分面数、碎片数
     * @param facetId 分面Id
     * @return 查询结果
     */
    public Result judgeFragmentByFacetId(Long facetId) {
        List<String> results = new ArrayList<>(); // 保存结果：子分面数，父分面数，碎片数
        try {
            List<FacetRelation> childFacets = facetRelationRepository.findByParentFacetId(facetId); // 找到所有子分面
            List<FacetRelation> parentFacets = facetRelationRepository.findByChildFacetId(facetId); // 找到所有父分面
            String facetInfo = "";
            results.add(childFacets.size() + "");
            results.add(parentFacets.size() + "");
            facetInfo = "该分面下存在文本碎片：" + assembleTextRepository.findByFacetId(facetId).size() +
                    "，图片碎片：" + assembleImageRepository.findByFacetId(facetId).size();
            results.add(facetInfo);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), results);
        } catch (Exception e) {
            return ResultUtil.error(ResultEnum.FRAGMENTQUERY_ERROR.getCode(), ResultEnum.FRAGMENTQUERY_ERROR.getMsg(), results);
        }
    }

    /**
     * 根据主题名，爬取文本和图片信息
     * @param topic 主题
     * @return 爬取结果
     * @throws Exception
     */
    public Result storeAllFragmentByTopic(Topic topic) throws Exception {
        // 判断是否已经爬取过该分面的碎片信息
        if (assembleTextRepository.findByTopicId(topic.getTopicId()).size() == 0 && assembleImageRepository.findByTopicId(topic.getTopicId()).size() == 0) {
            List<Object> list = spiderFragmentService.storeFragmentByTopic(topic);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), list);
        } else {
            logger.error("主题Id为：" + topic.getTopicId() + "，主题名为：" + topic.getTopicName() + "  的碎片数据已经爬取，无需重复爬取");
            return ResultUtil.error(ResultEnum.FRAGMENTDUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.FRAGMENTDUPLICATEDCRAWLER_ERROR.getMsg());
        }
    }

}
