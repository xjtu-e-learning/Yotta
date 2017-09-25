package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Domain;
import Yotta.spider.service.domain.DomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 领域控制器
 * 领域人工处理
 * Created by 18710 on 2017/8/11.
 */
@RestController
@RequestMapping("/domain")
public class DomainController {

    @Autowired
    private DomainService domainService;

    @GetMapping(value = "/getDomain")
    @ApiOperation(value = "查询所有领域", notes = "查询所有领域，不区分数据源")
    public ResponseEntity getDomain() {
        Result result = domainService.getDomain();
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getDomainBySourceID")
    @ApiOperation(value = "根据数据源查询领域", notes = "输入数据源，查询领域信息")
    public ResponseEntity getDomainBySourceId(@RequestParam(value = "sourceId", defaultValue = "1") Long sourceId) {
        Result result = domainService.getDomainBySourceId(sourceId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertDomain")
    @ApiOperation(value = "新增领域", notes = "新增领域")
    public ResponseEntity insertDomain(Domain domain) {
        Result result = domainService.insertDomain(domain);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/deleteDomain")
    @ApiOperation(value = "删除领域", notes = "删除领域")
    public ResponseEntity deleteDomain(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        Result result = domainService.deleteDomain(domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/updateDomain")
    @ApiOperation(value = "更新领域", notes = "更新领域")
    public ResponseEntity updateDomain(@RequestParam(value = "domainId", defaultValue = "1") Long domainId,
                                       Domain newDomain) {
        Result result = domainService.updateDomain(domainId, newDomain);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getDomainByPagingAndSorting")
    @ApiOperation(value = "分页查询领域", notes = "分页查询领域")
    public ResponseEntity getDomainByPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder) {
        Result result = domainService.getDomainByPagingAndSorting(page - 1, size, ascOrder);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getDomainBySourceIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询领域", notes = "根据查询条件，分页查询领域")
    public ResponseEntity getDomainBySourceIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "sourceId", defaultValue = "1") Long sourceId) {
        Result result = domainService.getDomainBySourceIdAndPagingAndSorting(page - 1, size, ascOrder, sourceId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



}
