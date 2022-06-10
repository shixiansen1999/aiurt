package com.aiurt.boot.modules.fault.controller;

import com.aiurt.boot.common.aspect.annotation.AutoLog;
import com.aiurt.boot.common.result.FaultCountResult;
import com.aiurt.boot.common.result.FaultLevelResult;
import com.aiurt.boot.common.result.FaultMonthResult;
import com.aiurt.boot.modules.fault.param.FaultCountParam;
import com.aiurt.boot.modules.fault.service.IFaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 *
 * 故障统计
 */

@Slf4j
@Api(tags="故障统计数据")
@RestController
@RequestMapping("/fault/faultCount")
public class FaultCountController {

    @Autowired
    private IFaultService faultService;

    @AutoLog(value = "故障统计数据")
    @ApiOperation(value = "故障统计数据", notes = "故障统计数据")
    @GetMapping(value = "/getFaultCount")
    public Result<List<FaultCountResult>> getFaultCount(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        if (param.getDayStart()==null && param.getDayEnd() == null) {
            //获取本月开始时间
            LocalDateTime of = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
            param.setDayStart(of);
            //获取当前时间
            LocalDateTime nowDate = now.atTime(23, 59, 59);
            param.setDayEnd(nowDate);
        }

        return faultService.getFaultCount(param);
    }

    @AutoLog(value = "故障统计数据导出")
    @ApiOperation(value = "故障统计数据导出", notes = "故障统计数据导出")
    @GetMapping(value = "/exportXls")
    public ModelAndView exportXls(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        if (param.getDayStart()==null && param.getDayEnd() == null) {
            //获取本月开始时间
            LocalDateTime of = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
            param.setDayStart(of);
            //获取当前时间
            LocalDateTime nowDate = now.atTime(0, 0, 0);
            param.setDayEnd(nowDate);
        }
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Result<List<FaultCountResult>> faultCount = faultService.getFaultCount(param);
        List<FaultCountResult> faultCountResults = faultCount.getResult();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "故障统计数据导出");
        mv.addObject(NormalExcelConstants.CLASS, FaultCountResult.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("故障统计数据导出",  "故障统计数据导出"));
        mv.addObject(NormalExcelConstants.DATA_LIST, faultCountResults);
        return mv;
    }

    /**
     * 各系统检修/自检对比
     * @param param
     * @return
     */
    @AutoLog(value = "各系统检修/自检对比")
    @ApiOperation(value = "各系统检修/自检对比", notes = "各系统检修/自检对比")
    @GetMapping(value = "/getContrast")
    public Result<List<FaultCountResult>> getContrast(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        if (param.getDayStart()==null && param.getDayEnd() == null) {
            //获取本月开始时间
            LocalDateTime of = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
            param.setDayStart(of);
            //获取当前时间
            LocalDateTime nowDate = now.atTime(23, 59, 59);
            param.setDayEnd(nowDate);
        }
        return faultService.getContrast(param);
    }

    /**
     * 各系统故障数比较
     * @param param
     * @return
     */
    @AutoLog(value = "各系统故障数比较")
    @ApiOperation(value = "各系统故障数比较", notes = "各系统故障数比较")
    @GetMapping(value = "/getPercentage")
    public Result<List<FaultCountResult>> getPercentage(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        if (param.getDayStart()==null && param.getDayEnd() == null) {
            //获取本月开始时间
            LocalDateTime of = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0, 0);
            param.setDayStart(of);
            //获取当前时间
            LocalDateTime nowDate = now.atTime(23, 59, 59);
            param.setDayEnd(nowDate);
        }
        return faultService.getPercentage(param);
    }

    /**
     * 单一系统检修/自检各月份故障分析
     * @param param
     * @return
     */
    @AutoLog(value = "单一系统检修/自检各月份故障分析")
    @ApiOperation(value = "单一系统检修/自检各月份故障分析", notes = "单一系统检修/自检各月份故障分析")
    @GetMapping(value = "/getFaultNumByMonth")
    public Result<List<FaultMonthResult>> getFaultNumByMonth(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        LocalDateTime of = LocalDateTime.of(now.getYear(), 1, 1, 0, 0, 0);
        param.setDayStart(of);
        LocalDateTime nowDate = now.atTime(23, 59, 59);
        param.setDayEnd(nowDate);
        Result<List<FaultMonthResult>> month = faultService.getFaultNumByMonth(param);
        return month;
    }

    /**
     * 设备故障总数同比分析
     * @param param
     * @return
     */
    @AutoLog(value = "设备故障总数同比分析")
    @ApiOperation(value = "设备故障总数同比分析", notes = "设备故障总数同比分析")
    @GetMapping(value = "/getFaultByMonth")
    public Result<List<FaultMonthResult>> getFaultByMonth(FaultCountParam param) {
        LocalDate now = LocalDate.now();
        LocalDateTime of = LocalDateTime.of(now.getYear(), 1, 1, 0, 0, 0);
        param.setDayStart(of);
        LocalDateTime nowDate = now.atTime(23, 59, 59);
        param.setDayEnd(nowDate);
        return faultService.getFaultByMonth(param);
    }

    /**
     * 首页一级故障
     * @param startTime
     * @param endTime
     * @return
     */
    @AutoLog(value = "首页一级故障")
    @ApiOperation(value = "首页一级故障", notes = "首页一级故障")
    @GetMapping(value = "/getFirstLevelFault")
    public Result<List<FaultLevelResult>> getFirstLevelFault(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        Result<List<FaultLevelResult>> firstLevelFault = faultService.getFirstLevelFault(startTime, endTime);
        return firstLevelFault;
    }

    /**
     * 首页二级故障
     * @param startTime
     * @param endTime
     * @return
     */
    @AutoLog(value = "首页二级故障")
    @ApiOperation(value = "首页二级故障", notes = "首页二级故障")
    @GetMapping(value = "/getSecondLevelFault")
    public Result<List<FaultLevelResult>> getSecondLevelFault(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        Result<List<FaultLevelResult>> firstLevelFault = faultService.getSecondLevelFault(startTime, endTime);
        return firstLevelFault;
    }

    /**
     * 首页三级故障
     * @param startTime
     * @param endTime
     * @return
     */
    @AutoLog(value = "首页三级故障")
    @ApiOperation(value = "首页三级故障", notes = "首页三级故障")
    @GetMapping(value = "/getThirdLevelFault")
    public Result<List<FaultLevelResult>> getThirdLevelFault(@RequestParam(name = "startTime", required = false) String startTime, @RequestParam(name = "endTime", required = false) String endTime) {
        Result<List<FaultLevelResult>> firstLevelFault = faultService.getThirdLevelFault(startTime, endTime);
        return firstLevelFault;
    }

}
