package com.aiurt.boot.screen.controller;

import com.aiurt.boot.screen.model.*;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
/**
 * @author JB
 * @Description: 大屏巡视模块控制层
 */
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
    public Result<ScreenImportantData> getImportantData(@ApiParam(name = "timeType", value = "看板时间类型,不传默认本周：1本周、2上周、3本月、4上月")
                                                                Integer timeType,
                                                        @ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割")
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
    public Result<ScreenStatistics> getStatisticsData(@ApiParam(name = "timeType", value = "看板时间类型,不传默认本周：1本周、2上周、3本月、4上月")
                                                              Integer timeType,
                                                      @ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割")
                                                              String lineCode) {
        ScreenStatistics statistics = screenService.getStatisticsData(timeType, lineCode);
        return Result.ok(statistics);
    }

    /**
     * 大屏巡视模块-巡视数据统计详情列表
     *
     * @param pageNo
     * @param pageSize
     * @param screenModule
     * @param lineCode
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视数据统计详情列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视数据统计详情列表", notes = "大屏巡视模块-巡视数据统计详情列表")
    @RequestMapping(value = "/statisticsDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<ScreenStatisticsTask>> getStatisticsDataList(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                     @ApiParam(name = "timeType", value = "看板时间类型,不传默认本周：1本周、2上周、3本月、4上月")
                                                                             Integer timeType,
                                                                     @ApiParam(name = "screenModule", value = "巡视数据统计模块标识，不传直接返空：1计划数、2完成数、3漏检数、4巡视异常数、5今日巡视数、6今日巡视完成数")
                                                                             Integer screenModule,
                                                                     @ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割") String lineCode) {
        Page<ScreenStatisticsTask> page = new Page<>(pageNo, pageSize);
        IPage<ScreenStatisticsTask> pageList = screenService.getStatisticsDataList(page, timeType, screenModule, lineCode);
        return Result.ok(pageList);
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
    public Result<IPage<ScreenStatisticsTask>> getStatisticsTaskInfo(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                    @ApiParam(name = "timeType", value = "看板时间类型,不传默认本周：1本周、2上周、3本月、4上月")
                                                                            Integer timeType,
                                                                    @ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割")
                                                                            String lineCode) {
        Page<ScreenStatisticsTask> page = new Page<>(pageNo, pageSize);
        IPage<ScreenStatisticsTask> pageList = screenService.getStatisticsTaskInfo(page,timeType, lineCode);
        return Result.ok(pageList);
    }

    /**
     * 大屏巡视模块-巡视任务完成情况
     *
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视任务完成情况", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视任务完成情况", notes = "大屏巡视模块-巡视任务完成情况")
    @RequestMapping(value = "/statisticsGraph", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<ScreenStatisticsGraph>> getStatisticsPieGraph(@ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割") String lineCode) {
        List<ScreenStatisticsGraph> list = screenService.getStatisticsGraph(lineCode);
        return Result.ok(list);
    }

    /**
     * 大屏巡视模块-温湿度
     *
     * @return
     */
    @AutoLog(value = "大屏巡视模块-温湿度", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-温湿度", notes = "大屏巡视模块-温湿度")
    @RequestMapping(value = "/temAndHum", method = {RequestMethod.GET})
    public Result<List<TemperatureHumidity>> getTemAndHum(@ApiParam(name = "date", value = "当天时间") String date) {
        List<TemperatureHumidity> list = screenService.getTemAndHum(date);
        return Result.ok(list);
    }

    @AutoLog(value = "大屏巡视模块-温湿度", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-温湿度测试", notes = "大屏巡视模块-温湿度")
    @RequestMapping(value = "/temAndHumTest", method = {RequestMethod.GET})
    public Result<List<TemperatureHumidity>> getTemAndHumTest(@ApiParam(name = "date", value = "当天时间") String date) {
        List<TemperatureHumidity> list = screenService.getTemAndHumTest(date);
        return Result.ok(list);
    }
}
