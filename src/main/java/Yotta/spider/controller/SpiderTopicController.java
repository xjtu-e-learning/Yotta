package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.service.SpiderTopicService;
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
public class SpiderTopicController {

    @Autowired
    private SpiderTopicService spiderTopicService;

    @GetMapping(value = "/getTopicByDomainId")
    @ApiOperation(value = "根据领域Id判断主题数据是否爬取", notes = "输入领域Id，判断主题数据是否爬取")
    public ResponseEntity getTopicByDomainId(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) {
        // 根据领域ID判断主题数据是否爬取
        Result result = spiderTopicService.getTopicByDomainId(domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping(value = "/storeTopicByDomainId")
    @ApiOperation(value = "根据领域Id爬取主题", notes = "输入领域Id，爬取主题及其上下位关系信息")
    public ResponseEntity storeByDomainID(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) throws Exception{
        // 根据领域ID爬取主题信息
        Result result = spiderTopicService.storeByDomainID(domainId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/storeTopicByDomainName", method = RequestMethod.GET)
    @ApiOperation(value = "根据领域名爬取主题", notes = "输入领域名，爬取主题及其上下位关系信息")
    public ResponseEntity storeByDomainName(@RequestParam(value = "domainName", defaultValue = "计算机科学史") String domainName) throws Exception {
        // 根据领域名爬取主题信息
        Result result = spiderTopicService.storeByDomainName(domainName);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
