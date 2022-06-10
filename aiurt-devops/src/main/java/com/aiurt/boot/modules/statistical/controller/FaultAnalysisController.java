package com.aiurt.boot.modules.statistical.controller;

import com.aiurt.boot.common.result.*;
import com.aiurt.boot.modules.fault.service.IFaultAnalysisReportService;
import com.aiurt.boot.modules.fault.service.IFaultRepairRecordService;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.fault.service.IOperationProcessService;
import com.aiurt.boot.modules.statistical.vo.FaultDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@Api(tags = "大屏-故障管理分析")
@RestController
@RequestMapping("/faultAnalysis")
public class FaultAnalysisController {


    @Autowired
    private IFaultAnalysisReportService faultAnalysisReportService;

    @Autowired
    private IFaultService faultService;

    @Autowired
    private IFaultRepairRecordService faultRepairRecordService;

    @Autowired
    private IOperationProcessService operationProcessService;

    /**
     * 根据故障编号查询弹窗信息-故障基本信息、维修记录、更换备件、分析报告、运转流程
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "根据故障编号查询故障基本信息", notes = "根据故障编号查询故障基本信息")
    @GetMapping(value = "/getFaultDetail")
    public Result<FaultDetailVo> getFaultDetail(@RequestParam(name = "code", required = true) String code) {
        try {
            FaultDetailVo faultDetailVo = new FaultDetailVo();
            FaultResult faultDetail = faultService.getFaultDetail(code);
            List<FaultRepairRecordResult> repairRecord = faultRepairRecordService.getRepairRecord(code);
            FaultAnalysisReportResult report = faultAnalysisReportService.getAnalysisReport(code);
            List<SpareResult> spareResults = faultRepairRecordService.changeSpare(code);
            List<OperationProcessResult> process = operationProcessService.getOperationProcess(code);

            faultDetailVo.setFaultDetail(faultDetail);
            faultDetailVo.setRepairRecord(repairRecord);
            faultDetailVo.setSpareResults(spareResults);
            faultDetailVo.setReport(report);
            faultDetailVo.setProcess(process);
            return Result.ok(faultDetailVo);
        } catch (Exception e) {
            return Result.error(0, "无数据");
        }
    }
}
