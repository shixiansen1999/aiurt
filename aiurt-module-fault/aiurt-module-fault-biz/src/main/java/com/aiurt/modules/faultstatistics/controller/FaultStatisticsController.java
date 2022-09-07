package com.aiurt.modules.faultstatistics.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultStatisticsDTO;
import com.aiurt.modules.faultanalysisreport.dto.SpareConsumeDTO;
import com.aiurt.modules.faultstatistics.service.FaultStatisticsService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zwl
 * @Title:
 * @Description: 首页故障单统计控制层
 * @date 2022/9/611:15
 */
@Api(tags = "首页故障单统计接口")
@RestController
@RequestMapping("/fault/statistics")
@Slf4j
public class FaultStatisticsController {

    @Resource
    private FaultStatisticsService faultStatisticsService;

    /**
     * 获取首页的故障单统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    @AutoLog(value = "首页-故障单统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-故障单统计", notes = "首页-故障单统计")
    @RequestMapping(value = "/faultList", method = RequestMethod.GET)
    public Result<FaultStatisticsDTO> getFaultList(@ApiParam(name = "startDate", value = "开始日期") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                @ApiParam(name = "endDate", value = "结束日期") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        FaultStatisticsDTO overviewInfo = faultStatisticsService.getFaultList(startDate, endDate);
        return Result.OK(overviewInfo);
    }

    @ApiOperation(value = "首页-备件消耗排行榜", notes = "首页-备件消耗排行榜")
    @RequestMapping(value = "/getSpareConsume", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "1-4:表示1-4季度，5-6：半年，年度", required = true, paramType = "query")
    })
    public Result<List<SpareConsumeDTO>> getSpareConsume(@RequestParam(value = "type", defaultValue = "1") String type) {
        return Result.OK();
    }
}
