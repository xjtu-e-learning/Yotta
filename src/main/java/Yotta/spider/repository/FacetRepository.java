package Yotta.spider.repository;

import Yotta.spider.domain.Facet;
import Yotta.spider.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * Created by yuanhao on 2017/5/3.
 */
public interface FacetRepository extends JpaRepository<Facet, Long>, JpaSpecificationExecutor<Facet> {

    List<Facet> findByTopicId(Long topicId);

    Facet findByTopicIdAndFacetName(Long topicId, String facetName);

    List<Facet> findByTopicIdAndFacetLayer(Long topicId, Long facetLayer);

    Facet findByTopicIdAndFacetLayerAndFacetName(Long topicId, Long facetLayer, String facetName);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Facet f set f.facetId = ?2, f.facetName = ?3, f.facetLayer = ?4, f.topicId = ?5 where f.facetId = ?1")
    void updateByFacetId(Long facetId, Long newFacetId, String facetName, Long facetLayer, Long topicId);

}
