package Yotta.spider.repository;

import Yotta.spider.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 主题的数据库操作接口：增删改查
 * 继承JpaRepository数据接口
 * Created by yuanhao on 2017/5/1.
 */
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    List<Topic> findByDomainId(Long domainId);

    List<Topic> findAll();

}
