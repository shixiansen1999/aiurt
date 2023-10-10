package com.aiurt.boot.screen.controller;

import com.aiurt.boot.screen.model.*;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.task.entity.TemperatureHumidity;
import com.aiurt.boot.task.param.TemHumParam;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysParamAPI;
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
    @Autowired
    private ISysParamAPI sysParamApi;
    /**
     * 大屏巡视模块-重要数据展示
     *
     * @return
     */
    @AutoLog(value = "大屏巡视模块-重要数据展示", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-重要数据展示", notes = "大屏巡视模块-重要数据展示")
    @RequestMapping(value = "/importantData", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<ScreenImportantData> getImportantData(@ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割")
                                                                String lineCode,
                                                        @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                        @ApiParam(name = "endDate", value = "结束时间") String endDate) {
        ScreenImportantData data = screenService.getImportantData(lineCode,startDate,endDate);
        return Result.ok(data);
    }

    /**
     * 大屏巡视模块-巡视数据统计
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视数据统计", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视数据统计", notes = "大屏巡视模块-巡视数据统计")
    @RequestMapping(value = "/statistics", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<ScreenStatistics> getStatisticsData(@ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割") String lineCode,
                                                      @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                      @ApiParam(name = "endDate", value = "结束时间") String endDate) {
        ScreenStatistics statistics = screenService.getStatisticsData(lineCode,startDate,endDate);
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
                                                                     @ApiParam(name = "screenModule", value = "巡视数据统计模块标识，不传直接返空：1计划数、2完成数、3漏检数、4巡视异常数、5今日巡视数、6今日巡视完成数、7未完成")
                                                                             Integer screenModule,
                                                                     @ApiParam(name = "lineCode", value = "线路编号") String lineCode,
                                                                     @ApiParam(name = "stationCode", value = "站点") String stationCode,
                                                                     @ApiParam(name = "username", value = "巡视人名称") String username,
                                                                     @ApiParam(name = "startDate", value = "开始日期") String startDate,
                                                                     @ApiParam(name = "endDate", value = "结束日期") String endDate) {
        Page<ScreenStatisticsTask> page = new Page<>(pageNo, pageSize);
        IPage<ScreenStatisticsTask> pageList = screenService.getStatisticsDataList(page, screenModule, lineCode,stationCode, username, startDate,endDate);
        return Result.ok(pageList);
    }

    /**
     * 大屏巡视模块-巡视数据统计任务列表
     *
     * @return
     */
    @AutoLog(value = "大屏巡视模块-巡视数据统计任务列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-巡视数据统计任务列表", notes = "大屏巡视模块-巡视数据统计任务列表")
    @RequestMapping(value = "/statisticsTaskInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<ScreenStatisticsTask>> getStatisticsTaskInfo(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                     @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                                     @ApiParam(name = "endDate", value = "结束时间") String endDate,
                                                                    @ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割")
                                                                            String lineCode) {
        Page<ScreenStatisticsTask> page = new Page<>(pageNo, pageSize);
        IPage<ScreenStatisticsTask> pageList = screenService.getStatisticsTaskInfo(page,startDate,endDate, lineCode);
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
    public Result<List<ScreenStatisticsGraph>> getStatisticsPieGraph(@ApiParam(name = "lineCode", value = "线路编号,多选的话英文逗号分割") String lineCode,
                                                                     @ApiParam(name = "startDate", value = "开始时间") String startDate,
                                                                     @ApiParam(name = "endDate", value = "结束时间") String endDate) {
        List<ScreenStatisticsGraph> list = screenService.getStatisticsGraph(lineCode,startDate,endDate);
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
    public Result<ScreenTemHum> getTemAndHum(TemHumParam temHumParam) {
        ScreenTemHum temHumScreenDTO = screenService.getTemAndHum(temHumParam);
        return Result.ok(temHumScreenDTO);
    }

    @AutoLog(value = "大屏巡视模块-温湿度", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "大屏巡视模块-温湿度测试", notes = "大屏巡视模块-温湿度")
    @RequestMapping(value = "/temAndHumTest", method = {RequestMethod.GET})
    public Result<List<TemperatureHumidity>> getTemAndHumTest(@ApiParam(name = "date", value = "当天时间") String date) {
        List<TemperatureHumidity> list = screenService.getTemAndHumTest(date);
        return Result.ok(list);
    }
}
