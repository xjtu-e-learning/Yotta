package Yotta.spider.repository;

import Yotta.spider.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程表格操作
 * Created by yuanhao on 2017/5/1.
 */
public interface DomainRepository extends JpaRepository<Domain, Long>, JpaSpecificationExecutor<Domain>{

    Domain findByDomainId(Long domainId);

    Domain findByDomainName(String domainName);

    Domain findByDomainNameAndSourceId(String domainName, Long sourceId);

    List<Domain> findBySourceId(Long sourceId);

    List<Domain> findAll();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Domain d set d.domainId = ?2, d.domainName = ?3, d.domainUrl = ?4, d.note = ?5, d.sourceId = ?6 where d.domainId = ?1")
    void updateByDomainId(Long domainId, Long newDomainId, String domainName, String domainUrl, String note, Long sourceId);

//    @Modifying(clearAutomatically = true)
//    @Transactional
//    @Query("delete from Facet f where f.termID = ?1 and f.facetName = ?2")
//    void deleteByTermIDAndFacetName(Long termID, String facetName);

}
