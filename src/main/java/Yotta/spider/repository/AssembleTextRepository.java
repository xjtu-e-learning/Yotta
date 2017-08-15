package Yotta.spider.repository;

import Yotta.spider.domain.AssembleText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by yuanhao on 2017/5/3.
 */
public interface AssembleTextRepository extends JpaRepository<AssembleText, Long> {

    List<AssembleText> findByTopicId(Long topicId);

}
