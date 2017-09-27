package Yotta.spider.repository;

import Yotta.spider.domain.Facet;
import Yotta.spider.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * Created by yuanhao on 2017/5/3.
 */
public interface FacetRepository extends JpaRepository<Facet, Long>{

    List<Facet> findByTopicId(Long topicId);

    List<Facet> findByTopicIdAndFacetLayer(Long topicId, Long facetLayer);

    Facet findByTopicIdAndFacetLayerAndFacetName(Long topicId, Long facetLayer, String facetName);

}
