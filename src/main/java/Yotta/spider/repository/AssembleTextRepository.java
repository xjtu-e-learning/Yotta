package Yotta.spider.repository;

import Yotta.spider.domain.AssembleText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * Created by yuanhao on 2017/5/3.
 */
public interface AssembleTextRepository extends JpaRepository<AssembleText, Long> {

    List<AssembleText> findByTopicId(Long topicId);

    List<AssembleText> findByFacetId(Long facetId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByFacetId(Long facetId);

}
