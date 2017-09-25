package Yotta.spider.service.source;

import Yotta.common.domain.Result;
import Yotta.common.domain.ResultEnum;
import Yotta.spider.domain.Source;
import Yotta.spider.repository.SourceRepository;
import Yotta.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 获取并处理“数据源”信息
 * Created by 18710 on 2017/8/14.
 */
@Service
public class SourceService {

    // 打印信息
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SourceRepository sourceRepository;

    /**
     * 获得所有数据源
     * @return 查询结果 Result
     */
    public Result getSources() {
        List<Source> sources = sourceRepository.findAll();
        if (sources.size() > 0) {
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), sources);
        } else {
            return ResultUtil.error(ResultEnum.SOURCE_ERROR.getCode(), ResultEnum.SOURCE_ERROR.getMsg());
        }
    }

}
