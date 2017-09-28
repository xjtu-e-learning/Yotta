package Yotta.spider.repository;

import Yotta.spider.domain.FacetRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yuanhao on 2017/5/3.
 */
public interface FacetRelationRepository extends JpaRepository<FacetRelation, Long>{

    List<FacetRelation> findByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByTopicId(Long topicId);

}
