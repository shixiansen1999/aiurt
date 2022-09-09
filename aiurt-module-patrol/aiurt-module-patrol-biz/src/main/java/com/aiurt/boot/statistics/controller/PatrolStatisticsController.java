package com.aiurt.boot.statistics.controller;

import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.statistics.dto.IndexScheduleDTO;
import com.aiurt.boot.statistics.dto.IndexTaskDTO;
import com.aiurt.boot.statistics.model.*;
import com.aiurt.boot.statistics.service.PatrolStatisticsService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

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
    public Result<PatrolSituation> getOverviewInfo(@ApiParam(name = "startDate", value = "开始日期")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam("startDate") Date startDate,
                                                   @ApiParam(name = "endDate", value = "结束日期")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                   @RequestParam("endDate") Date endDate) {
        PatrolSituation situation = patrolStatisticsService.getOverviewInfo(startDate, endDate);
        return Result.ok(situation);
    }

    /**
     * 获取首页的巡视列表
     *
     * @return
     */
    @AutoLog(value = "首页-巡视列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-巡视列表", notes = "首页-巡视列表")
    @RequestMapping("/getPatrolList")
    public Result<IPage<PatrolIndexTask>> getPatrolList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        @Validated PatrolCondition patrolCondition) {
        Page<PatrolIndexTask> page = new Page<PatrolIndexTask>(pageNo, pageSize);
        IPage<PatrolIndexTask> pageList = patrolStatisticsService.getIndexPatrolList(page, patrolCondition);
        return Result.ok(pageList);
    }

    /**
     * 获取首页的巡视任务列表
     *
     * @return
     */
    @AutoLog(value = "首页-获取首页的巡视任务列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-获取首页的巡视任务列表", notes = "首页-获取首页的巡视任务列表")
    @RequestMapping("/getIndexTaskList")
    public Result<IPage<IndexTaskInfo>> getIndexTaskList(@Validated @RequestBody IndexTaskDTO abnormalDTO) {
        Page<IndexTaskInfo> page = new Page<>(abnormalDTO.getPageNo(), abnormalDTO.getPageSize());
        IPage<IndexTaskInfo> pageList = patrolStatisticsService.getIndexTaskList(page, abnormalDTO);
        return Result.ok(pageList);
    }

    /**
     * 获取首页的日程的巡检列表
     *
     * @return
     */
    @AutoLog(value = "首页-获取首页的日程的巡检列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "首页-获取首页的日程的巡检列表", notes = "首页-获取首页的日程的巡检列表")
    @RequestMapping("/getScheduleList")
    public Result<IPage<ScheduleTask>> getScheduleList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @Validated IndexScheduleDTO indexScheduleDTO) {
        Page<ScheduleTask> page = new Page<>(pageNo, pageSize);
        IPage<ScheduleTask> pageList = patrolStatisticsService.getScheduleList(page, indexScheduleDTO);
        return Result.ok(pageList);
    }
}
