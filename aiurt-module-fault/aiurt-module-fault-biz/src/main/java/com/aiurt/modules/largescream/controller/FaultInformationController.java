package com.aiurt.modules.largescream.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.fault.dto.FaultIndexDTO;
import com.aiurt.modules.fault.dto.FaultLargeCountDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
import com.aiurt.modules.largescream.service.FaultInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 功能描述
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

    @AutoLog(value = "综合大屏-故障信息统计详情", operateType = 1, operateTypeAlias = "查询", permissionUrl = "")
    @ApiOperation(value = "综合大屏-故障信息统计详情", notes = "综合大屏-故障信息统计详情")
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
}
