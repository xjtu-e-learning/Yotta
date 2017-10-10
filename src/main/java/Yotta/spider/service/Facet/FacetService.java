package Yotta.spider.service.Facet;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Facet;
import Yotta.spider.domain.FacetRelation;
import Yotta.spider.domain.Topic;
import Yotta.spider.domain.TopicRelation;
import Yotta.spider.repository.*;
import Yotta.utils.ResultUtil;
import org.slf4j.Logger;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 分面
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
    @Autowired
    private FacetRelationRepository facetRelationRepository;
    @Autowired
    private AssembleTextRepository assembleTextRepository;
    @Autowired
    private AssembleImageRepository assembleImageRepository;


    /**
     * 根据分面关系Id 删除分面关系
     * @param facetRelationId 分面id
     * @return 删除结果
     */
    public Result deleteFacetRelation(Long facetRelationId) {
        try {
            facetRelationRepository.deleteAllByRelationId(facetRelationId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除成功");
        } catch (Exception e) {
            logger.error("删除失败");
            return ResultUtil.error(ResultEnum.FACETRELATIONDELETE_ERROR.getCode(), ResultEnum.FACETRELATIONDELETE_ERROR.getMsg());
        }
    }

    /**
     * 获取分页分面关系数据，按照分面ID排序 (带查询条件：根据同一主题Id下的数据)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @param domainId 领域Id (查询条件)
     * @return 分页排序的数据
     */
    public Result getFacetRelationByTopicIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long topicId) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "topicId");
        // lamada表达式的写法
        Page<FacetRelation> facetRelationPage = facetRelationRepository.findAll(
                (Root<FacetRelation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("topicId").as(Long.class), topicId);
                }, pageable);
        return facetRelationPageJudge(facetRelationPage);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询领域失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param facetRelationPage 分页查询结果
     * @return 返回查询结果
     */
    public Result facetRelationPageJudge(Page<FacetRelation> facetRelationPage) {
        if (facetRelationPage.getTotalElements() == 0) { // 没有查到数据
            return ResultUtil.error(ResultEnum.FACETRELATIONQUERY_ERROR.getCode(), ResultEnum.FACETRELATIONQUERY_ERROR.getMsg());
        } else if (facetRelationPage.getNumber() >= facetRelationPage.getTotalPages()) { // 查询的页数超过最大页数
            return ResultUtil.error(ResultEnum.PAGE_ERROR.getCode(), ResultEnum.PAGE_ERROR.getMsg());
        }
        // 返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetRelationPage);
    }

    /**
     * 根据分面Id 删除分面
     * 同时删除分面关系表，碎片表中该分面的所有数据
     * @param facetId 分面id
     * @return 删除结果
     */
    public Result deleteFacet(Long facetId) {
        try {
            facetRepository.delete(facetId);
            facetRelationRepository.deleteAllByChildFacetIdOrParentFacetId(facetId, facetId);
            assembleTextRepository.deleteAllByFacetId(facetId);
            assembleImageRepository.deleteAllByFacetId(facetId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除成功");
        } catch (Exception e) {
            logger.error("删除失败");
            return ResultUtil.error(ResultEnum.FACETDELETE_ERROR.getCode(), ResultEnum.FACETDELETE_ERROR.getMsg());
        }
    }

    /**
     * 根据分面Id 更新分面
     * 删除分面对应的分面关系
     * @param facetId 分面id
     * @param newFacet 新增加的分面信息
     * @return 更新结果
     */
    public Result updateFacet(Long facetId, Facet newFacet) {
        try {
            facetRepository.updateByFacetId(facetId, newFacet.getFacetId(), newFacet.getFacetName(), newFacet.getFacetLayer(), newFacet.getTopicId());
            facetRelationRepository.deleteAllByChildFacetIdOrParentFacetId(facetId, facetId); // 修改分面，删除分面关系
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "更新成功");
        } catch (Exception e) {
            logger.error("更新失败");
            return ResultUtil.error(ResultEnum.FACETUPDATE_ERROR.getCode(), ResultEnum.FACETUPDATE_ERROR.getMsg());
        }
    }

    /**
     * 插入分面下的子分面及子主题关系
     * @param facet 插入子分面
     * @param parentFacetName 父分面名
     * @return 插入结果
     */
    public Result insertFacetUnderFacet(Facet facet, String parentFacetName) {
        // 插入分面名不能为空
        if (facet.getFacetName() == null || "".equals(facet.getFacetName()) || facet.getFacetName().length() == 0) {
            return ResultUtil.error(ResultEnum.FACETINSERTNOTNULL_ERROR.getCode(), ResultEnum.FACETINSERTNOTNULL_ERROR.getMsg());
        }
        // 插入分面不存在，可以插入
        if (facetRepository.findByTopicIdAndFacetName(facet.getTopicId(), facet.getFacetName()) == null) {
            // 设置分面关系
            FacetRelation facetRelation = new FacetRelation();
            facetRelation.setTopicId(facet.getTopicId());
            facetRelation.setChildFacetName(facet.getFacetName());
            facetRelation.setParentFacetName(parentFacetName);
            Facet parentFacet = facetRepository.findByTopicIdAndFacetName(facet.getTopicId(), parentFacetName);
            Long parentFacetLayer = parentFacet.getFacetLayer();
            facetRelation.setParentFacetLayer(parentFacetLayer);
            facetRelation.setChildFacetLayer(parentFacetLayer + 1); // 子主题的layer是父主题的layer加1
            // 插入分面
            facet.setFacetLayer(parentFacetLayer + 1);
            Facet facet1 = facetRepository.save(facet);
            // 插入分面关系
            facetRelation.setChildFacetId(facet.getFacetId());
            facetRelation.setParentFacetId(parentFacet.getFacetId());
            FacetRelation facetRelation1 = facetRelationRepository.save(facetRelation);

            if (facet1 != null && facetRelation1 != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facet1);
            } else if (facet1 == null) { // 分面插入失败
                return ResultUtil.error(ResultEnum.FACETINSERT_ERROR.getCode(), ResultEnum.FACETINSERT_ERROR.getMsg());
            } else { // 分面关系插入失败
                return ResultUtil.error(ResultEnum.FACETRELATIONINSERT_ERROR.getCode(), ResultEnum.FACETRELATIONINSERT_ERROR.getMsg());
            }
        }
        // 插入分面已存在
        return ResultUtil.error(ResultEnum.FACETINSERTDUPLICATE_ERROR.getCode(), ResultEnum.FACETINSERTDUPLICATE_ERROR.getMsg());
    }

    /**
     * 插入主题下的分面
     * @param facet 插入分面
     * @return 插入结果
     */
    public Result insertFacetUnderTopic(Facet facet) {
        // 插入分面名不能为空
        if (facet.getFacetName() == null || "".equals(facet.getFacetName()) || facet.getFacetName().length() == 0) {
            return ResultUtil.error(ResultEnum.FACETINSERTNOTNULL_ERROR.getCode(), ResultEnum.FACETINSERTNOTNULL_ERROR.getMsg());
        }
        // 插入分面不存在，可以插入
        if (facetRepository.findByTopicIdAndFacetName(facet.getTopicId(), facet.getFacetName()) == null) {
            // 插入分面
            facet.setFacetLayer(1L);
            Facet facet1 = facetRepository.save(facet);
            if (facet1 != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facet1);
            } else { // 插入分面失败
                return ResultUtil.error(ResultEnum.FACETINSERT_ERROR.getCode(), ResultEnum.FACETINSERT_ERROR.getMsg());
            }
        }
        // 插入分面已存在
        return ResultUtil.error(ResultEnum.FACETINSERTDUPLICATE_ERROR.getCode(), ResultEnum.FACETINSERTDUPLICATE_ERROR.getMsg());
    }

    /**
     * 获取分页分面数据，按照分面ID排序 (带查询条件：根据同一主题Id下的数据)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @param domainId 领域Id (查询条件)
     * @return 分页排序的数据
     */
    public Result getFacetByTopicIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long topicId) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "topicId");
        // lamada表达式的写法
        Page<Facet> facetPage = facetRepository.findAll(
                (Root<Facet> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("topicId").as(Long.class), topicId);
                }, pageable);
        return facetPageJudge(facetPage);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询领域失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param facetPage 分页查询结果
     * @return 返回查询结果
     */
    public Result facetPageJudge(Page<Facet> facetPage) {
        if (facetPage.getTotalElements() == 0) { // 没有查到数据
            return ResultUtil.error(ResultEnum.FACETQUERY_ERROR.getCode(), ResultEnum.FACETQUERY_ERROR.getMsg());
        } else if (facetPage.getNumber() >= facetPage.getTotalPages()) { // 查询的页数超过最大页数
            return ResultUtil.error(ResultEnum.PAGE_ERROR.getCode(), ResultEnum.PAGE_ERROR.getMsg());
        }
        // 返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetPage);
    }

    /**
     * 根据 主题Id 得到所有分面
     * @param topicId 主题ID
     * @return 查询结果
     */
    public Result getFacetByTopicId(Long topicId) {
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), facetRepository.findByTopicId(topicId));
    }

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
     * 根据主题，爬取主题的分面和分面关系信息
     * @param topic 主题
     * @return 爬取结果
     * @throws Exception
     */
    public Result storeAllFacetAndContentByTopic(Topic topic) throws Exception {
        if (facetRepository.findByTopicId(topic.getTopicId()).size() == 0) { // 判断是否已经爬取过该主题的分面信息
            List<Object> list = spiderFacetService.storeFacetAndRelationByTopic(topic);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), list);
        } else {
            logger.error("主题Id为：" + topic.getTopicId() + "，主题名为：" + topic.getTopicName() + "  的分面和碎片数据已经爬取，无需重复爬取");
            return ResultUtil.error(ResultEnum.FACETDUPLICATEDCRAWLER_ERROR.getCode(), ResultEnum.FACETDUPLICATEDCRAWLER_ERROR.getMsg());
        }
    }


}
