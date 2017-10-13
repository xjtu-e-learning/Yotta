package Yotta.spider.repository;

import Yotta.spider.domain.FacetRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分面关系
 * Created by yuanhao on 2017/5/3.
 */
public interface FacetRelationRepository extends JpaRepository<FacetRelation, Long>, JpaSpecificationExecutor<FacetRelation> {

    List<FacetRelation> findByTopicId(Long topicId);

    List<FacetRelation> findByParentFacetId(Long parentFacetId);

    List<FacetRelation> findByChildFacetId(Long childFacetId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByChildFacetIdOrParentFacetId(Long childFacetId, Long parentFacetId); // 删除子分面id或者父分面id为某个id的分面关系

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByRelationId(Long relationId);

}
