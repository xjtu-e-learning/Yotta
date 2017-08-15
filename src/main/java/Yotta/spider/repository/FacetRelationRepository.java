package Yotta.spider.repository;

import Yotta.spider.domain.FacetRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yuanhao on 2017/5/3.
 */
public interface FacetRelationRepository extends JpaRepository<FacetRelation, Long>{

    List<FacetRelation> findByTopicId(Long topicId);

}
