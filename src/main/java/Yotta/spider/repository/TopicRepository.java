package Yotta.spider.repository;

import Yotta.spider.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 主题的数据库操作接口：增删改查
 * 继承JpaRepository数据接口
 * Created by yuanhao on 2017/5/1.
 */
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    List<Topic> findByDomainId(Long domainId);

    List<Topic> findByDomainIdAndTopicLayer(Long domainId, Long topicLayer);

    Topic findByDomainIdAndTopicName(Long domainId, String topicName);

    Topic findByDomainIdAndTopicNameAndTopicLayer(Long domainId, String topicName, Long topicLayer);

    List<Topic> findAll();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Topic t set t.topicId = ?2, t.topicName = ?3, t.topicUrl = ?4, t.topicLayer = ?5, t.domainId = ?6 where t.topicId = ?1")
    void updateByTopicId(Long topicId, Long newTopicId, String topicName, String topicUrl, Long topicLayer, Long domainId);

}
