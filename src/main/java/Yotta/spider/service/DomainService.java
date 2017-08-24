package Yotta.spider.service;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Domain;
import Yotta.spider.repository.DomainRepository;
import Yotta.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 获取并处理domain领域信息
 * Created by 18710 on 2017/8/11.
 */
@Service
public class DomainService {

    // 打印信息
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DomainRepository domainRepository;

    /**
     * 插入领域
     * @param domain 需要插入的领域
     * @return 插入结果
     */
    public Result insertDomain(Domain domain) {
        if (domainRepository.findByDomainNameAndSourceId(domain.getDomainName(), domain.getSourceId()) == null) {
            Domain domain1 = domainRepository.save(domain);
            if (domain1 != null) {
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domain1);
            } else {
                return ResultUtil.error(ResultEnum.DOMAININSERT_ERROR.getCode(), ResultEnum.DOMAININSERT_ERROR.getMsg());
            }
        } else {
            if (domain.getDomainName() == null || "".equals(domain.getDomainName()) || domain.getDomainName().length() == 0) {
                // 插入领域名不能为空
                return ResultUtil.error(ResultEnum.DOMAININSERTNOTNULL_ERROR.getCode(), ResultEnum.DOMAININSERTNOTNULL_ERROR.getMsg());
            } else {
                // 不能插入重复的
                return ResultUtil.error(ResultEnum.DOMAININSERTDUPLICATE_ERROR.getCode(), ResultEnum.DOMAININSERTDUPLICATE_ERROR.getMsg());
            }

        }
    }

    /**
     * 根据领域 Id 删除领域
     * @param domainId 领域id
     * @return 删除结果
     */
    public Result deleteDomain(Long domainId) {
        try {
            domainRepository.delete(domainId);
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "删除成功");
        } catch (Exception e) {
            return ResultUtil.error(ResultEnum.DOMAINDELETE_ERROR.getCode(), ResultEnum.DOMAINDELETE_ERROR.getMsg());
        }

    }

    /**
     * 根据领域Id 更新领域
     * @param domainId 领域id
     * @param newDomain 新增加的领域信息
     * @return 更新结果
     */
    public Result updateDomain(Long domainId, Domain newDomain) {
        try {
            domainRepository.updateByDomainId(domainId, newDomain.getDomainId(), newDomain.getDomainName(), newDomain.getDomainUrl(), newDomain.getNote(), newDomain.getSourceId());
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "更新成功");
        } catch (Exception e) {
            logger.error("更新失败");
            return ResultUtil.error(ResultEnum.DOMAINUPDATE_ERROR.getCode(), ResultEnum.DOMAINUPDATE_ERROR.getMsg());
        }
    }

    /**
     * 查询所有领域名
     * @return 查询结果
     */
    public Result getDomain() {
        List<Domain> domains = domainRepository.findAll();
        if (domains.size() > 0) {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domains);
        } else {
            return ResultUtil.error(ResultEnum.DOMAINQUERY_ERROR.getCode(), ResultEnum.DOMAINQUERY_ERROR.getMsg());
        }
    }

    /**
     * 根据数据源 Id 查询所有领域名
     * @param sourceId
     * @return
     */
    public Result getDomainBySourceId(Long sourceId) {
        List<Domain> domains = domainRepository.findBySourceId(sourceId);
        if (domains.size() > 0) {
            domains.forEach(domain -> logger.info("查询结果为：" + domain.toString()));
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domains);
        } else {
            logger.info("该数据源不存在对应的领域信息");
            return ResultUtil.error(ResultEnum.DOMAINQUERY_ERROR.getCode(), ResultEnum.DOMAINQUERY_ERROR.getMsg());
        }
    }

    /**
     * 获取分页领域数据，按照领域ID排序 (不带查询条件)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @return 分页排序的数据
     */
    public Result getDomainByPagingAndSorting(Integer page, Integer size, boolean ascOrder) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "domainId");  // 分页和排序条件，默认按照id排序
        Page<Domain> domainPage = domainRepository.findAll(pageable);
        return domainPageJudge(domainPage);
    }

    /**
     * 获取分页领域数据，按照领域ID排序 (带查询条件：根据同一数据源Id下的数据)
     * @param page 第几页的数据
     * @param size 每页数据的大小
     * @param ascOrder 是否升序
     * @param sourceId 数据源Id (查询条件)
     * @return 分页排序的数据
     */
    public Result getDomainBySourceIdAndPagingAndSorting(Integer page, Integer size, boolean ascOrder, Long sourceId) {
        // 页数是从0开始计数的
        Sort.Direction direction = Sort.Direction.ASC;
        if (!ascOrder) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = new PageRequest(page, size, direction, "domainId");
        // lamada表达式的写法
        Page<Domain> domainPage = domainRepository.findAll(
                (Root<Domain> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
                    return criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
                }, pageable);
        return domainPageJudge(domainPage);
//        Page<Domain> domainPage = domainRepository.findAll(new Specification<Domain>(){
//            @Override
//            public Predicate toPredicate(Root<Domain> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                // 多个条件查询
////                Predicate p1 = criteriaBuilder.equal(root.get("domainId").as(Long.class), domainId);
////                Predicate p2 = criteriaBuilder.equal(root.get("domainName").as(String.class), domainName);
////                Predicate p3 = criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
////                query.where(criteriaBuilder.and(p1,p2,p3));
////                return query.getRestriction();
//                return criteriaBuilder.equal(root.get("sourceId").as(Long.class), sourceId);
//            }
//        }, pageable);
    }

    /**
     * 根据分页查询的结果，返回不同的状态。
     * 1. totalElements为 0：说明没有查到数据，查询领域失败
     * 2. number大于totalPages：说明查询的页数大于最大页数，返回失败
     * 3. 成功返回分页数据
     * @param domainPage 分页查询结果
     * @return 返回查询结果
     */
    public Result domainPageJudge(Page<Domain> domainPage) {
        if (domainPage.getTotalElements() == 0) { // 没有查到数据
            return ResultUtil.error(ResultEnum.DOMAINQUERY_ERROR.getCode(), ResultEnum.DOMAINQUERY_ERROR.getMsg());
        } else if (domainPage.getNumber() >= domainPage.getTotalPages()) { // 查询的页数超过最大页数
            return ResultUtil.error(ResultEnum.PAGE_ERROR.getCode(), ResultEnum.PAGE_ERROR.getMsg());
        }
        // 返回查询的内容
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), domainPage);
    }

}
