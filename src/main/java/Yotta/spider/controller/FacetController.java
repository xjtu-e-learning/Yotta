package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.service.Facet.FacetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by yuanhao on 2017/9/27.
 */
@RestController
@RequestMapping("/facet")
public class FacetController {

    @Autowired
    private FacetService facetService;

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

}
