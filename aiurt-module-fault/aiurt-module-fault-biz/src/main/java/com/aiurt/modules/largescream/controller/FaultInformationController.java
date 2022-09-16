package com.aiurt.modules.largescream.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultDataStatisticsDTO;
import com.aiurt.modules.fault.dto.FaultLargeCountDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
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
    @ApiOperation(value="故障信息统计", notes="故障信息统计")
    @GetMapping(value = "/queryLargeFaultInformation")
    public Result<FaultLargeCountDTO> queryFaultCount(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月") @RequestParam("boardTimeType")Integer boardTimeType,
                                                 @ApiParam(name = "lineCode",value = "线路")@RequestParam("lineCode")String lineCode){
        FaultLargeCountDTO faultLargeCountDTO = faultInformationService.queryLargeFaultInformation(boardTimeType,lineCode);
        return Result.ok(faultLargeCountDTO);
    }

    @AutoLog(value = "综合大屏-故障信息统计列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障信息统计列表", notes = "综合大屏-故障信息统计列表")
    @RequestMapping(value = "/getLargeFaultInfo", method = RequestMethod.GET)
    public Result<List<FaultLargeInfoDTO>> getLargeFaultInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月") @RequestParam("boardTimeType")Integer boardTimeType,
                                                             @ApiParam(name = "lineCode",value = "线路")@RequestParam("lineCode")String lineCode){
        List<FaultLargeInfoDTO> largeFaultInfo = faultInformationService.getLargeFaultInfo(boardTimeType, lineCode);
        return Result.ok(largeFaultInfo);
    }

    @AutoLog(value = "综合大屏-线路故障统计", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-线路故障统计", notes = "综合大屏-线路故障统计")
    @RequestMapping(value = "/getLargeLineFaultInfo", method = RequestMethod.GET)
    public Result<List<FaultLargeLineInfoDTO>> getLargeLineFaultInfo(@ApiParam(name = "boardTimeType", value = "1:本周 2:上周 3:本月 4:上月") @RequestParam("boardTimeType")Integer boardTimeType)
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


    @AutoLog(value = "综合大屏-故障时长趋势图", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障时长趋势图", notes = "综合大屏-故障时长趋势图")
    @RequestMapping(value = "/getLargeFaultTime", method = RequestMethod.GET)
    public Result<List<FaultMonthTimeDTO>> getLargeFaultTime( @ApiParam(name = "lineCode",value = "线路")@RequestParam("lineCode")String lineCode)
    {
        List<FaultMonthTimeDTO> largeFaultTime = faultInformationService.getLargeFaultTime(lineCode);
        return Result.ok(largeFaultTime);
    }


    @AutoLog(value = "大屏-故障数据分析-故障数据统计")
    @ApiOperation(value="大屏-故障数据分析-故障数据统计", notes="大屏-故障数据分析-故障数据统计")
    @GetMapping(value = "/queryLargeFaultDataCount")
    public Result<FaultDataAnalysisCountDTO> queryLargeFaultDataCount(@ApiParam(name = "lineCode",value = "线路")@RequestParam("lineCode")String lineCode){
        FaultDataAnalysisCountDTO faultDataAnalysisCountDTO = faultInformationService.queryLargeFaultDataCount(lineCode);
        return Result.ok(faultDataAnalysisCountDTO);
    }

    @AutoLog(value = "大屏-故障数据分析-故障数据统计列表", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "大屏-故障数据分析-故障数据统计列表", notes = "大屏-故障数据分析-故障数据统计列表")
    @RequestMapping(value = "/getLargeFaultDataInfo", method = RequestMethod.GET)
    public Result<List<FaultDataAnalysisInfoDTO>> getLargeFaultDataInfo(@ApiParam(name = "lineCode",value = "线路")@RequestParam("lineCode")String lineCode){
        List<FaultDataAnalysisInfoDTO> largeFaultDataInfo = faultInformationService.getLargeFaultDataInfo(lineCode);
        return Result.ok(largeFaultDataInfo);
    }
}
