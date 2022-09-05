package com.aiurt.boot.statistics.controller;

import com.aiurt.boot.statistics.dto.PatrolSituation;
import com.aiurt.boot.statistics.service.PatrolStatisticsService;
import com.aiurt.common.aspect.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "巡检首页统计")
@RestController
@RequestMapping("/patrolStatistics")
@Slf4j
public class PatrolStatisticsController {
    @Autowired
    private PatrolStatisticsService patrolStatisticsService;

    /**
     * 获取首页的巡视概况信息
     *
     * @return
     */
    @AutoLog(value = "首页-巡视概况", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-巡视概况", notes = "首页-巡视概况")
    @RequestMapping("/overviewInfo")
    public PatrolSituation getOverviewInfo(@ApiParam(name = "startDate", value = "开始日期")
                                           @RequestParam("startDate") Date startDate,
                                           @ApiParam(name = "endDate", value = "结束日期")
                                           @RequestParam("endDate") Date endDate) {
        PatrolSituation situation = patrolStatisticsService.getOverviewInfo(startDate, endDate);
        return situation;
    }
}
