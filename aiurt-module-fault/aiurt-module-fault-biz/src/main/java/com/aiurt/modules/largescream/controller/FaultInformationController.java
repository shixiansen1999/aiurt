package com.aiurt.modules.largescream.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.largescream.service.FaultInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 大屏综合看板
 *
 * @author: qkx
 * @date: 2022-09-13 14:37
 */
@Api(tags = "大屏综合看板故障信息统计接口")
@RestController
@RequestMapping("/fault/faultInformation")
@Slf4j
public class FaultInformationController {
    @Resource
    private FaultInformationService faultInformationService;

    @AutoLog(value = "综合大屏-故障信息统计")
    @ApiOperation(value="综合大屏-故障信息统计", notes="综合大屏-故障信息统计")
    @GetMapping(value = "/queryLargeFaultInformation")
    public Result<FaultLargeCountDTO> queryFaultCount(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                 @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        FaultLargeCountDTO faultLargeCountDTO = faultInformationService.queryLargeFaultInformation(boardTimeType,lineCode);
        return Result.ok(faultLargeCountDTO);
    }

    @AutoLog(value = "综合大屏-故障信息统计详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障信息统计详情", notes = "综合大屏-故障信息统计详情")
    @RequestMapping(value = "/getLargeFaultDatails", method = RequestMethod.GET)
    public Result<List<FaultLargeInfoDTO>> getLargeFaultDatails(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam("boardTimeType")Integer boardTimeType,
                                                                @ApiParam(name = "faultModule", value = "故障信息统计详情模块：1:总故障数 2:未解决故障数 3:当日新增 4:当日已解决") @RequestParam("faultModule")Integer faultModule,
                                                             @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        List<FaultLargeInfoDTO> largeFaultDatails = faultInformationService.getLargeFaultDatails(boardTimeType, faultModule, lineCode);
        return Result.ok(largeFaultDatails);
    }

    @AutoLog(value = "综合大屏-故障信息统计列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障信息统计列表", notes = "综合大屏-故障信息统计列表")
    @RequestMapping(value = "/getLargeFaultInfo", method = RequestMethod.GET)
    public Result<List<FaultLargeInfoDTO>> getLargeFaultInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                             @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationService.getLargeFaultInfo(boardTimeType, lineCode);
        return Result.ok(largeFaultInfo);
    }

    @AutoLog(value = "综合大屏-线路故障统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-线路故障统计", notes = "综合大屏-线路故障统计")
    @RequestMapping(value = "/getLargeLineFaultInfo", method = RequestMethod.GET)
    public Result<List<FaultLargeLineInfoDTO>> getLargeLineFaultInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType)
    {
        List<FaultLargeLineInfoDTO> largeLineFaultInfo = faultInformationService.getLargeLineFaultInfo(boardTimeType);
        return Result.ok(largeLineFaultInfo);
    }

    /**
     * 年度故障维修情况统计
     * @author: lkj
     * @return List<FaultDataStatisticsDTO>
     */
    @AutoLog(value = "综合大屏-年度故障维修情况统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-年度故障维修情况统计", notes = "综合大屏-年度故障维修情况统计")
    @RequestMapping(value = "/getYearFault", method = RequestMethod.GET)
    public Result<List<FaultDataStatisticsDTO>> getYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        List<FaultDataStatisticsDTO> yearFault = faultInformationService.getYearFault(faultDataStatisticsDTO);
        return Result.ok(yearFault);
    }

    /**
     * 各子系统年度故障维修情况统计
     * @author: lkj
     * @return List<FaultDataStatisticsDTO>
     */
    @AutoLog(value = "综合大屏-各子系统年度故障维修情况统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-各子系统年度故障维修情况统计", notes = "综合大屏-各子系统年度故障维修情况统计")
    @RequestMapping(value = "/getSystemYearFault", method = RequestMethod.GET)
    public Result<List<FaultDataStatisticsDTO>> getSystemYearFault(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        List<FaultDataStatisticsDTO> systemYearFault = faultInformationService.getSystemYearFault(faultDataStatisticsDTO);
        return Result.ok(systemYearFault);
    }

    /**
     * 故障分析饼状图数据
     * @author: lkj
     * @return FaultDataStatisticsDTO
     */
    @AutoLog(value = "综合大屏-故障分析饼状图数据", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障分析饼状图数据", notes = "综合大屏-故障分析饼状图数据")
    @RequestMapping(value = "/getFaultAnalysis", method = RequestMethod.GET)
    public Result<FaultDataStatisticsDTO> getFaultAnalysis(FaultDataStatisticsDTO faultDataStatisticsDTO) {
        FaultDataStatisticsDTO faultAnalysis = faultInformationService.getFaultAnalysis(faultDataStatisticsDTO);
        return Result.ok(faultAnalysis);
    }


    /**
     * 故障时长趋势图接口
     * @param lineCode
     * @return
     */
    @AutoLog(value = "综合大屏-故障时长趋势图", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障时长趋势图", notes = "综合大屏-故障时长趋势图")
    @RequestMapping(value = "/getLargeFaultTime", method = RequestMethod.GET)
    public Result<List<FaultMonthTimeDTO>> getLargeFaultTime( @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode)
    {
        List<FaultMonthTimeDTO> largeFaultTime = faultInformationService.getLargeFaultTime(lineCode);
        return Result.ok(largeFaultTime);
    }

    /**
     * 故障数据统计接口
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    @AutoLog(value = "大屏-故障数据分析-故障数据统计")
    @ApiOperation(value="大屏-故障数据分析-故障数据统计", notes="大屏-故障数据分析-故障数据统计")
    @GetMapping(value = "/queryLargeFaultDataCount")
    public Result<FaultDataAnalysisCountDTO> queryLargeFaultDataCount(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                                      @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        FaultDataAnalysisCountDTO faultDataAnalysisCountDTO = faultInformationService.queryLargeFaultDataCount(boardTimeType,lineCode);
        return Result.ok(faultDataAnalysisCountDTO);
    }

    /**
     * 故障数据统计详情
     * @param boardTimeType
     * @param faultModule
     * @param lineCode
     * @return
     */
    @AutoLog(value = "大屏-故障数据分析-故障数据统计详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "大屏-故障数据分析-故障数据统计详情", notes = "大屏-故障数据分析-故障数据统计详情")
    @RequestMapping(value = "/getLargeFaultDataDatails", method = RequestMethod.GET)
    public Result<List<FaultLargeInfoDTO>> getLargeFaultDataDatails(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                                @ApiParam(name = "faultModule", value = "故障数据统计详情模块：1:故障总数 2:未修复故障数 3:本周增加 4:本周修复 5:今日增加 6:今日修复",defaultValue = "1") @RequestParam(value="faultModule",required = false)Integer faultModule,
                                                                @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        List<FaultLargeInfoDTO> largeFaultDataDatails = faultInformationService.getLargeFaultDataDatails(boardTimeType,faultModule, lineCode);
        return Result.ok(largeFaultDataDatails);
    }

    /**
     * 故障数据统计列表接口
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    @AutoLog(value = "大屏-故障数据分析-故障数据统计列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "大屏-故障数据分析-故障数据统计列表", notes = "大屏-故障数据分析-故障数据统计列表")
    @RequestMapping(value = "/getLargeFaultDataInfo", method = RequestMethod.GET)
    public Result<List<FaultDataAnalysisInfoDTO>> getLargeFaultDataInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                                        @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        List<FaultDataAnalysisInfoDTO> largeFaultDataInfo = faultInformationService.getLargeFaultDataInfo(boardTimeType,lineCode);
        return Result.ok(largeFaultDataInfo);
    }

    /**
     * 大屏-故障数据分析-故障超时等级详情接口
     * @param boardTimeType
     * @param lineCode
     * @return
     */
    @AutoLog(value = "大屏-故障数据分析-故障超时等级详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "大屏-故障数据分析-故障超时等级详情", notes = "大屏-故障数据分析-故障超时等级详情")
    @RequestMapping(value = "/getFaultLevelInfo", method = RequestMethod.GET)
    public Result<List<FaultLevelDTO>> getFaultLevelInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType,
                                                         @ApiParam(name = "lineCode",value = "线路")@RequestParam(value = "lineCode",required = false)String lineCode){
        List<FaultLevelDTO> faultLevelInfo = faultInformationService.getFaultLevelInfo(boardTimeType, lineCode);
        return Result.OK(faultLevelInfo);
    }


    /**
     * 子系统可靠度接口
     * @return
     */
    @AutoLog(value = "综合大屏-子系统可靠度", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-子系统可靠度", notes = "综合大屏-子系统可靠度")
    @RequestMapping(value = "/getSystemReliability", method = RequestMethod.GET)
    public Result<List<FaultSystemReliabilityDTO>> getSystemReliability(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月",defaultValue = "1") @RequestParam(value="boardTimeType",required = false)Integer boardTimeType){
        List<FaultSystemReliabilityDTO> systemReliability = faultInformationService.getSystemReliability(boardTimeType);
        return Result.ok(systemReliability);
    }
}
