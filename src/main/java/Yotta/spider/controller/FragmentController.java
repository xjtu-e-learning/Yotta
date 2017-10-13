package Yotta.spider.controller;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Topic;
import Yotta.spider.service.fragment.FragmentService;
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
@RequestMapping("/fragment")
public class FragmentController {

    @Autowired
    private FragmentService fragmentService;

    @RequestMapping(value = "/judgeFragmentByFacetId")
    @ApiOperation(value = "根据分面id，返回该分面的子分面数、父分面数、碎片数", notes = "根据分面id，返回该分面的子分面数、父分面数、碎片数")
    public ResponseEntity judgeFragmentByFacetId(
            @RequestParam(value = "facetId", defaultValue = "1") Long facetId
    ) {
        // 根据分面id，返回该分面的子分面数、父分面数、碎片数
        Result result = fragmentService.judgeFragmentByFacetId(facetId);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @RequestMapping(value = "/storeAllFragmentByTopic")
    @ApiOperation(value = "根据主题名，爬取文本和图片信息", notes = "根据主题名，爬取文本和图片信息")
    public ResponseEntity storeAllFragmentByTopic(Topic topic) throws Exception {
        // 根据主题名，爬取文本和图片信息
        Result result = fragmentService.storeAllFragmentByTopic(topic);
        if (result.getCode() != ResultEnum.SUCCESS.getCode()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
