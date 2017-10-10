package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Facet;
import Yotta.spider.domain.Topic;
import Yotta.spider.service.Facet.FacetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分面接口
 * Created by yuanhao on 2017/9/27.
 */
@RestController
@RequestMapping("/facet")
public class FacetController {

    @Autowired
    private FacetService facetService;

    @GetMapping(value = "/deleteFacetRelation")
    @ApiOperation(value = "删除分面关系", notes = "删除分面关系")
    public ResponseEntity deleteFacetRelation(@RequestParam(
            value = "facetRelationId", defaultValue = "1") Long facetRelationId
    ) {
        Result result = facetService.deleteFacetRelation(facetRelationId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getFacetRelationByTopicIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询分面关系", notes = "根据查询条件，分页查询分面关系")
    public ResponseEntity getFacetRelationByTopicIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "topicId", defaultValue = "1") Long topicId) {
        Result result = facetService.getFacetRelationByTopicIdAndPagingAndSorting(page - 1, size, ascOrder, topicId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/deleteFacet")
    @ApiOperation(value = "删除分面", notes = "删除分面")
    public ResponseEntity deleteFacet(@RequestParam(
            value = "facetId", defaultValue = "1") Long facetId
    ) {
        Result result = facetService.deleteFacet(facetId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/updateFacet")
    @ApiOperation(value = "更新分面", notes = "更新分面")
    public ResponseEntity updateFacet(
            @RequestParam(value = "facetId", defaultValue = "1") Long facetId,
            Facet newFacet
    ) {
        Result result = facetService.updateFacet(facetId, newFacet);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertFacetUnderFacet")
    @ApiOperation(value = "新增分面下面的子分面", notes = "新增分面下的子分面")
    public ResponseEntity insertFacetUnderFacet(Facet facet, String parentFacetName) {
        Result result = facetService.insertFacetUnderFacet(facet, parentFacetName);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertFacetUnderTopic")
    @ApiOperation(value = "新增主题下面的分面", notes = "新增主题下的分面")
    public ResponseEntity insertFacetUnderTopic(Facet facet) {
        Result result = facetService.insertFacetUnderTopic(facet);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getFacetByTopicIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询分面", notes = "根据查询条件，分页查询分面")
    public ResponseEntity getFacetByTopicIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "topicId", defaultValue = "1") Long topicId) {
        Result result = facetService.getFacetByTopicIdAndPagingAndSorting(page - 1, size, ascOrder, topicId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getFacetByTopicId")
    @ApiOperation(value = "根据主题Id获取分面数据", notes = "输入主题Id，获取分面数据")
    public ResponseEntity getFacetByTopicId(@RequestParam(value = "topicId", defaultValue = "1") Long topicId) {
        // 根据主题ID获取分面数据
        Result result = facetService.getFacetByTopicId(topicId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/judgeFacetByTopicId")
    @ApiOperation(value = "根据主题Id，获取该主题的子主题数、父主题数、分面数", notes = "根据主题Id，获取该主题的子主题数、父主题数、分面数")
    public ResponseEntity judgeFacetByTopicId(
            @RequestParam(value = "topicId", defaultValue = "1") Long topicId
    ) {
        // 根据主题，返回该主题的子主题数、父主题数、分面数
        Result result = facetService.judgeFacetByTopicId(topicId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/storeAllFacetAndContentByTopic")
    @ApiOperation(value = "根据主题，爬取主题的分面及分面关系", notes = "根据主题，爬取主题的分面及分面关系")
    public ResponseEntity storeAllFacetAndContentByTopic(Topic topic) throws Exception {
        // 根据主题，爬取主题的分面及分面关系
        Result result = facetService.storeAllFacetAndContentByTopic(topic);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
