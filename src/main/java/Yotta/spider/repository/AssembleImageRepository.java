package Yotta.spider.repository;

import Yotta.spider.domain.AssembleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by yuanhao on 2017/5/3.
 */
public interface AssembleImageRepository extends JpaRepository<AssembleImage, Long> {

    List<AssembleImage> findByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByTopicId(Long topicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    void deleteAllByFacetId(Long facetId);

}
