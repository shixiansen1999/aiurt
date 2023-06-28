package com.aiurt.boot.report.controller;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.report.model.FailureOrgReport;
import com.aiurt.boot.report.model.FailureReport;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.model.dto.LineOrStationDTO;
import com.aiurt.boot.report.model.dto.SystemMonthDTO;
import com.aiurt.boot.report.service.PatrolReportService;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private ISysParamAPI sysParamApi;
    /**
     * 统计报表-巡视数据统计
     * @param report
     * @return
     */
    @AutoLog(value = "统计报表-巡视数据统计", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-巡视数据统计", notes = "统计报表-巡视数据统计")
    @RequestMapping(value = "/patrolTaskList", method = {RequestMethod.GET, RequestMethod.POST})
    public Result<IPage<PatrolReport>> getStatisticsDate(PatrolReportModel report,
                                                         @RequestParam(name="orgCodes",required = false) String orgCodes,
                                                         @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        if (StrUtil.isNotEmpty(orgCodes)) {
            report.setOrgCodeList(Arrays.asList(StrUtil.split(orgCodes, ",")));
        }
        Page<PatrolReport> pageList = new Page<>(pageNo, pageSize);
        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            pageList = reportService.getDeviceTaskDate(pageList, report);

        } else {
            pageList = reportService.getTaskDate(pageList, report);
        }
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
    public Result<IPage<FailureReport>> getFailureReport( @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                         @RequestParam(name = "lineCode",required = false) String lineCode,
                                                         @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                                         @RequestParam(name = "startTime",required = false) String startTime,
                                                         @RequestParam(name = "endTime",required = false) String endTime,
                                                          @RequestParam(name = "systemCode",required = false) String systemCode,
                                                         HttpServletRequest req) {
        Page<FailureReport> page = new Page<FailureReport>(pageNo, pageSize);
        List<String> systemCodes = new ArrayList<>();
        if (StrUtil.isNotEmpty(systemCode)) {
            systemCodes.addAll(Arrays.asList(StrUtil.split(systemCode, ",")));
        }
        IPage<FailureReport> pages = reportService.getFailureReport(page,lineCode,stationCode,startTime,endTime, systemCodes);
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
    public Result<List<SystemMonthDTO>> getMonthNum(@RequestParam(name = "lineCode",required = false) String lineCode,
                                              @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                              @RequestParam(name = "systemCode",required = false) String systemCode,
                                              @RequestParam(name = "startTime",required = false) String startTime,
                                              @RequestParam(name = "endTime",required = false) String endTime) {
        List<String> systemCodes = new ArrayList<>();
        if (StrUtil.isNotEmpty(systemCode)) {
            systemCodes.addAll(Arrays.asList(StrUtil.split(systemCode, ",")));
        }
        List<SystemMonthDTO> monthDtos = reportService.getMonthNum(lineCode,stationCode,systemCodes,startTime,endTime);
        return Result.ok(monthDtos);
    }
    /**
     * 统计报表-子系统故障列表-年图数据
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-班组故障列表-年图数据", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-班组故障列表-年图数据", notes = "统计报表-班组故障列表-年图数据")
    @RequestMapping(value = "/monthOrgReport", method = {RequestMethod.GET})
    public Result<List<SystemMonthDTO>> getMonthOrgNum(@RequestParam(name = "lineCode",required = false) String lineCode,
                                                 @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                                 @RequestParam(name = "systemCode",required = false) List<String> systemCode ,
                                                 @RequestParam(name = "orgCodes",required = false) String orgCodes,
                                                 @RequestParam(name = "startTime",required = false) String startTime,
                                                 @RequestParam(name = "endTime",required = false) String endTime) {
        List<String> orgCodeList = new ArrayList<>();
        if (StrUtil.isNotEmpty(orgCodes)) {
            orgCodeList.addAll(Arrays.asList(StrUtil.split(orgCodes, ",")));
        }
        List<SystemMonthDTO> monthDtos = reportService.getMonthOrgNum(lineCode,stationCode,systemCode,startTime,endTime,orgCodeList);
        return Result.ok(monthDtos);
    }
    /**
     * 统计报表-班组故障列表
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-班组故障列表", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-班组故障列表", notes = "统计报表-班组故障列表")
    @RequestMapping(value = "/failureOrgReport", method = {RequestMethod.GET})
    public Result<IPage<FailureOrgReport>> getFailureOrgReport( @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                               @RequestParam(name = "lineCode",required = false) String lineCode,
                                                               @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                                               @RequestParam(name = "subsystemCode",required = false) List<String> systemCode,
                                                               @RequestParam(name = "startTime",required = false) String startTime,
                                                               @RequestParam(name = "endTime",required = false) String endTime,
                                                                @RequestParam(name = "orgCodes",required = false) String orgCodes,
                                                                HttpServletRequest req) {
        Page<FailureOrgReport> page = new Page<FailureOrgReport>(pageNo, pageSize);
        List<String> orgCodeList = new ArrayList<>();
        if (StrUtil.isNotEmpty(orgCodes)) {
            orgCodeList.addAll(Arrays.asList(StrUtil.split(orgCodes, ",")));
        }
        IPage<FailureOrgReport> pages = reportService.getFailureOrgReport(page,lineCode,stationCode,startTime,endTime,systemCode,orgCodeList);
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
    public ModelAndView reportExport(HttpServletRequest request, PatrolReportModel reportReqVO,String exportField) {
        return reportService.reportExport(request, reportReqVO,exportField);
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
                                     @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                     @RequestParam(name = "startTime",required = false) String startTime,
                                     @RequestParam(name = "endTime",required = false) String endTime,
                                     @RequestParam(name = "systemCode",required = false) String systemCode,
                                     @RequestParam(name = "exportField",required = false)String exportField) {
        List<String> systemCodes = new ArrayList<>();
        if (StrUtil.isNotEmpty(systemCode)) {
            systemCodes.addAll(Arrays.asList(StrUtil.split(systemCode, ",")));
        }
        return reportService.reportSystemExport(request,lineCode,stationCode,startTime,endTime,exportField,systemCodes);
    }
        /**
         * 统计分析-班组故障报表导出
         *
         * @param request
         * @return
         */
        @AutoLog(value = "统计报表-班组故障报表导出", operateType = 6, operateTypeAlias = "导出")
        @ApiOperation(value = "统计报表-班组故障列表导出", notes = "统计报表-班组故障列表导出")
        @GetMapping(value = "/reportOrgExport")
        public ModelAndView reportOrgExport(HttpServletRequest request,
                                            @RequestParam(name = "lineCode",required = false) String lineCode,
                                            @RequestParam(name = "stationCode",required = false) List<String> stationCode,
                                            @RequestParam(name = "systemCode",required = false)  List<String> systemCode,
                                            @RequestParam(name = "startTime",required = false) String startTime,
                                            @RequestParam(name = "endTime",required = false) String endTime,
                                            @RequestParam(name = "orgcodes",required = false) String orgcodes,
                                            @RequestParam(name = "exportField",required = false)String exportField) {
            List<String> orgCodeList = new ArrayList<>();
            if (StrUtil.isNotEmpty(orgcodes)) {
                orgCodeList.addAll(Arrays.asList(StrUtil.split(orgcodes, ",")));
            }
            return reportService.reportOrgExport(request,lineCode,stationCode,startTime,endTime,systemCode,exportField,orgCodeList);

    }
    /**
     * 线路下拉框
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-线路下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-线路下拉框", notes = "统计报表-线路下拉框")
    @GetMapping(value = "/selectLine")
    public List<LineOrStationDTO> selectLine() {
        return reportService.selectLine();

    }
    /**
     * 站点下拉框
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-站点下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-站点下拉框", notes = "统计报表-站点下拉框")
    @GetMapping(value = "/selectStation")
    public List<LineOrStationDTO> selectStation(@RequestParam(name = "lineCode",required = false) String lineCode) {
        return reportService.selectStation(lineCode);

    }
    /**
     * 子系统下拉框
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-子系统下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-子系统下拉框", notes = "统计报表-子系统下拉框")
    @GetMapping(value = "/selectSystem")
    public List<LineOrStationDTO> selectSystem() {
        return reportService.selectSystem();

    }

    /**
     * 班组下拉框
     * @param
     * @return
     */
    @AutoLog(value = "统计报表-班组下拉框", operateType = 1, operateTypeAlias = "查询")
    @ApiOperation(value = "统计报表-班组下拉框", notes = "统计报表-班组下拉框")
    @GetMapping(value = "/selectDepart")
    public List<LineOrStationDTO> selectDepart(@RequestParam(name = "lineCode",required = false) String lineCode) {
        return reportService.selectDepart(lineCode);

    }
}
