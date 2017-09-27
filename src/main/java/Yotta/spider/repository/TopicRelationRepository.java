package Yotta.spider.repository;

import Yotta.spider.domain.TopicRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 主题间关系的数据库操作接口：增删改查
 * 继承JpaRepository数据接口
 * Created by yuanhao on 2017/5/1.
 */
public interface TopicRelationRepository extends JpaRepository<TopicRelation, Long>, JpaSpecificationExecutor<TopicRelation> {

    List<TopicRelation> findByDomainId(Long domainId);

    List<TopicRelation> findByChildTopicId(Long childTopicId);

    List<TopicRelation> findByParentTopicId(Long parentTopicId);

    List<TopicRelation> findAll();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update TopicRelation t set t.childTopicId = ?2, t.parentTopicId = ?3 where t.relationId = ?1")
    void updateByRelationId(Long relationId, Long childTopicId, Long parentTopicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update TopicRelation t set t.childTopicId = ?2 where t.relationId = ?1")
    void updateByRelationId(Long relationId, Long childTopicId);

}
