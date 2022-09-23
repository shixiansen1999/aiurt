package com.aiurt.boot.report.controller;

import com.aiurt.boot.report.model.FailureOrgReport;
import com.aiurt.boot.report.model.FailureReport;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.model.dto.MonthDTO;
import com.aiurt.boot.report.service.PatrolReportService;
import com.aiurt.boot.screen.model.ScreenStatisticsTask;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */
@Api(tags = "统计报表")
@RestController
@RequestMapping("/report")
@Slf4j
public class PatrolReportController {
    @Autowired
    private PatrolReportService reportService;
    /**
     * 统计报表-巡视数据统计
     * @param report
     * @return
     */
    @AutoLog(value = "统计报表-巡视数据统计", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-巡视数据统计", notes = "统计报表-巡视数据统计")
    @RequestMapping(value = "/patrolTaskList", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<PatrolReport>> getStatisticsDate(PatrolReportModel report,
                                                         @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Page<PatrolReport> pageList = new Page<>(pageNo, pageSize);
        pageList = reportService.getTaskDate(pageList, report);

        return Result.ok(pageList);
    }
    /**
     * 统计报表-子系统故障列表
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-子系统故障列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-子系统故障列表", notes = "统计报表-子系统故障列表")
    @RequestMapping(value = "/failureReport", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<List<FailureReport>> getFailureReport(@RequestParam(name = "lineCode",required = false) String lineCode,
                                                         @RequestParam(name = "stationCode",required = false) String stationCode,
                                                         @RequestParam(name = "startTime",required = false) String startTime,
                                                         @RequestParam(name = "endTime",required = false) String endTime,
                                                         HttpServletRequest req) {

        List<FailureReport> pages = reportService.getFailureReport(lineCode,stationCode,startTime,endTime);
        return Result.ok(pages);
    }
    /**
     * 统计报表-子系统故障列表-年图数据
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-子系统故障列表-年图数据", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-子系统故障列表-年图数据", notes = "统计报表-子系统故障列表-年图数据")
    @RequestMapping(value = "/monthReport", method = {RequestMethod.GET})
    public Result<List<MonthDTO>> getMonthNum(@RequestParam(name = "lineCode",required = false) String lineCode,
                                                   @RequestParam(name = "stationCode",required = false) String stationCode) {
        List<MonthDTO> monthDTOS = reportService.getMonthNum(lineCode,stationCode);
        return Result.ok(monthDTOS);
    }
    /**
     * 统计报表-子系统故障列表-年图数据
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-班组故障列表-年图数据", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-班组故障列表-年图数据", notes = "统计报表-班组故障列表-年图数据")
    @RequestMapping(value = "/monthOrgReport", method = {RequestMethod.GET})
    public Result<List<MonthDTO>> getMonthOrgNum(@RequestParam(name = "lineCode",required = false) String lineCode,
                                                 @RequestParam(name = "stationCode",required = false) String stationCode,
                                                 @RequestParam(name = "systemCode",required = false) String systemCode) {
        List<MonthDTO> monthDTOS = reportService.getMonthOrgNum(lineCode,stationCode,systemCode);
        return Result.ok(monthDTOS);
    }
    /**
     * 统计报表-班组故障列表
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-班组故障列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-班组故障列表", notes = "统计报表-班组故障列表")
    @RequestMapping(value = "/failureOrgReport", method = {RequestMethod.GET})
    public Result<List<FailureOrgReport>> getFailureOrgReport(@RequestParam(name = "lineCode",required = false) String lineCode,
                                                               @RequestParam(name = "stationCode",required = false) String stationCode,
                                                               @RequestParam(name = "systemCode",required = false) String systemCode,
                                                               @RequestParam(name = "startTime",required = false) String startTime,
                                                               @RequestParam(name = "endTime",required = false) String endTime,
                                                                HttpServletRequest req) {
        List<FailureOrgReport> pages = reportService.getFailureOrgReport(lineCode,stationCode,startTime,endTime,systemCode);
        return Result.ok(pages);
    }

    /**
     * 统计报表-巡检报表导出
     * @param reportReqVO
     * @return
     */
    @AutoLog(value = "统计报表-巡检报表导出", operateType = 6, operateTypeAlias = "导出")
    @ApiOperation(value = "统计报表-巡检报表导出", notes = "统计报表-巡检报表导出")
    @GetMapping(value = "/reportExport")
    public ModelAndView export(HttpServletRequest request, PatrolReportModel reportReqVO) {
        return reportService.reportExport(request, reportReqVO);
    }
    /**
     * 统计分析-子系统故障列表导出
     *
     * @param request
     * @return
     */
    @AutoLog(value = "统计报表-子系统故障列表导出", operateType = 6, operateTypeAlias = "导出")
    @ApiOperation(value = "统计报表-子系统故障列表导出", notes = "统计报表-子系统故障列表导出")
    @GetMapping(value = "/reportSystemExport")
    public ModelAndView reportExport(HttpServletRequest request,
                                     @RequestParam(name = "lineCode",required = false) String lineCode,
                                     @RequestParam(name = "stationCode",required = false) String stationCode,
                                     @RequestParam(name = "startTime",required = false) String startTime,
                                     @RequestParam(name = "endTime",required = false) String endTime) {
        return reportService.reportSystemExport(request,lineCode,stationCode,startTime,endTime);
    }
        /**
         * 统计分析-班组故障报表导出
         *
         * @param request
         * @return
         */
        @AutoLog(value = "统计报表-班组故障报表导出", operateType = 6, operateTypeAlias = "导出")
        @ApiOperation(value = "统计报表-子系统故障列表导出", notes = "统计报表-子系统故障列表导出")
        @GetMapping(value = "/reportOrgExport")
        public ModelAndView reportOrgExport(HttpServletRequest request,
                                            @RequestParam(name = "lineCode",required = false) String lineCode,
                                            @RequestParam(name = "stationCode",required = false) String stationCode,
                                            @RequestParam(name = "systemCode",required = false) String systemCode,
                                            @RequestParam(name = "startTime",required = false) String startTime,
                                            @RequestParam(name = "endTime",required = false) String endTime) {
            return reportService.reportOrgExport(request,lineCode,stationCode,startTime,endTime,systemCode);

    }
}
