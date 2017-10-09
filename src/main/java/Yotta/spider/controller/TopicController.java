package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Topic;
import Yotta.spider.service.topic.TopicService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  主题控制器
 *  主题自动爬取，不是手动
 * Created by 18710 on 2017/8/9.
 */
@RestController
@RequestMapping(value="/topic")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping(value = "/deleteTopicRelation")
    @ApiOperation(value = "删除主题", notes = "删除主题")
    public ResponseEntity deleteTopicRelation(@RequestParam(
            value = "topicRelationId", defaultValue = "1") Long topicRelationId
    ) {
        Result result = topicService.deleteTopicRelation(topicRelationId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getTopicRelationByDomainIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询主题关系", notes = "根据查询条件，分页查询主题关系")
    public ResponseEntity getTopicRelationByDomainIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        Result result = topicService.getTopicRelationByDomainIdAndPagingAndSorting(page - 1, size, ascOrder, domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/deleteTopic")
    @ApiOperation(value = "删除主题", notes = "删除主题")
    public ResponseEntity deleteTopic(@RequestParam(
            value = "topicId", defaultValue = "1") Long topicId
    ) {
        Result result = topicService.deleteTopic(topicId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/updateTopic")
    @ApiOperation(value = "更新主题", notes = "更新主题")
    public ResponseEntity updateTopic(
            @RequestParam(value = "topicId", defaultValue = "1") Long topicId,
            Topic newTopic
    ) {
        Result result = topicService.updateTopic(topicId, newTopic);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertTopicUnderTopic")
    @ApiOperation(value = "新增主题下面的子主题", notes = "新增主题下的子主题")
    public ResponseEntity insertTopicUnderTopic(Topic topic, String parentTopicName) {
        Result result = topicService.insertTopicUnderTopic(topic, parentTopicName);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/insertTopicUnderDomain")
    @ApiOperation(value = "新增领域下面的主题", notes = "新增领域下的主题")
    public ResponseEntity insertTopicUnderDomain(Topic topic) {
        Result result = topicService.insertTopicUnderDomain(topic);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getTopicByDomainIdAndPagingAndSorting")
    @ApiOperation(value = "根据查询条件，分页查询主题", notes = "根据查询条件，分页查询主题")
    public ResponseEntity getTopicByDomainIdAndPagingAndSorting(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "3") Integer size,
            @RequestParam(value = "ascOrder", defaultValue = "true") boolean ascOrder,
            @RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        Result result = topicService.getTopicByDomainIdAndPagingAndSorting(page - 1, size, ascOrder, domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/getTopicByDomainId")
    @ApiOperation(value = "根据领域Id获取主题数据", notes = "输入领域Id，获取主题数据")
    public ResponseEntity getTopicByDomainId(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        // 根据领域ID获取主题数据
        Result result = topicService.getTopicByDomainId(domainId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/judgeTopicByDomainId")
    @ApiOperation(value = "根据领域Id判断主题数据是否爬取", notes = "输入领域Id，判断主题数据是否爬取")
    public ResponseEntity judgeTopicByDomainId(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        // 根据领域ID判断主题数据是否爬取
        Result result = topicService.judgeTopicByDomainId(domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/storeTopicByDomainId")
    @ApiOperation(value = "根据领域Id爬取主题", notes = "输入领域Id，爬取主题及其上下位关系信息")
    public ResponseEntity storeByDomainID(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) throws Exception{
        // 根据领域ID爬取主题信息
        Result result = topicService.storeByDomainID(domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/storeTopicByDomainName", method = RequestMethod.GET)
    @ApiOperation(value = "根据领域名爬取主题", notes = "输入领域名，爬取主题及其上下位关系信息")
    public ResponseEntity storeByDomainName(@RequestParam(value = "domainName", defaultValue = "计算机科学史") String domainName) throws Exception {
        // 根据领域名爬取主题信息
        Result result = topicService.storeByDomainName(domainName);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
