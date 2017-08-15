package Yotta.spider.repository;

import Yotta.spider.domain.TopicRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 主题间关系的数据库操作接口：增删改查
 * 继承JpaRepository数据接口
 * Created by yuanhao on 2017/5/1.
 */
public interface TopicRelationRepository extends JpaRepository<TopicRelation, Long>{

    List<TopicRelation> findByDomainId(Long domainId);

}
