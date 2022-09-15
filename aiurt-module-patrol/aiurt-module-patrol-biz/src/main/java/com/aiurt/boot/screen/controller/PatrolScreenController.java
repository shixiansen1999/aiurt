package com.aiurt.boot.screen.controller;

import com.aiurt.boot.screen.model.ScreenImportantData;
import com.aiurt.boot.screen.model.ScreenStatistics;
import com.aiurt.boot.screen.model.ScreenStatisticsGraph;
import com.aiurt.boot.screen.model.ScreenStatisticsTask;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.common.aspect.annotation.AutoLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "大屏巡视模块")
@RestController
@RequestMapping("/patrolScreen")
@Slf4j
public class PatrolScreenController {

    @Autowired
    private PatrolScreenService screenService;

    /**
     * 大屏巡视模块-重要数据展示
     *
     * @param timeType
     * @return
     */
    @AutoLog(value = "大屏巡视模块-重要数据展示", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-重要数据展示", notes = "大屏巡视模块-重要数据展示")
    @RequestMapping(value = "/importantData", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<ScreenImportantData> getImportantData(@ApiParam(name = "timeType", value = "看板时间类型：1本周、2上周、3本月、4上月")
                                                        @RequestParam("timeType") Integer timeType,
                                                        @ApiParam(name = "lineCode", value = "线路编号")
                                                                String lineCode) {
        ScreenImportantData data = screenService.getImportantData(timeType, lineCode);
        return Result.ok(data);
    }

    /**
     * 大屏巡视模块-巡视数据统计
     *
     * @param timeType
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视数据统计", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视数据统计", notes = "大屏巡视模块-巡视数据统计")
    @RequestMapping(value = "/statistics", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<ScreenStatistics> getStatisticsData(@ApiParam(name = "timeType", value = "看板时间类型：1本周、2上周")
                                                      @RequestParam("timeType") Integer timeType,
                                                      @ApiParam(name = "lineCode", value = "线路编号")
                                                              String lineCode) {
        ScreenStatistics statistics = screenService.getStatisticsData(timeType, lineCode);
        return Result.ok(statistics);
    }

    /**
     * 大屏巡视模块-巡视数据统计任务列表
     *
     * @param timeType
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视数据统计任务列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视数据统计任务列表", notes = "大屏巡视模块-巡视数据统计任务列表")
    @RequestMapping(value = "/statisticsTaskInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<ScreenStatisticsTask>> getStatisticsTaskInfo(@ApiParam(name = "timeType", value = "看板时间类型：1本周、2上周")
                                                                    @RequestParam("timeType") Integer timeType,
                                                                    @ApiParam(name = "lineCode", value = "线路编号")
                                                                            String lineCode) {
        List<ScreenStatisticsTask> list = screenService.getStatisticsTaskInfo(timeType, lineCode);
        return Result.ok(list);
    }

    /**
     * 大屏巡视模块-巡视任务完成情况
     *
     * @param timeType
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视任务完成情况", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视任务完成情况", notes = "大屏巡视模块-巡视任务完成情况")
    @RequestMapping(value = "/statisticsGraph", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<ScreenStatisticsGraph>> getStatisticsPieGraph(@ApiParam(name = "timeType", value = "看板时间类型：1本周、2上周")
                                                                        @RequestParam("timeType") Integer timeType,
                                                                     @ApiParam(name = "lineCode", value = "线路编号")
                                                                                String lineCode) {
        List<ScreenStatisticsGraph> list = screenService.getStatisticsGraph(timeType, lineCode);
        return Result.ok(list);
    }
}
