package com.aiurt.modules.faultproducereport.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.aiurt.modules.faultproducereport.service.IFaultProduceReportExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date: 2023-02-23
 * @Version: V1.0
 */
@Api(tags = "生产日报")
@RestController
@RequestMapping("/faultproducereport/faultProduceReport")
@Slf4j
public class FaultProduceReportExportController{
    @Autowired
    private IFaultProduceReportExportService faultProduceReportExportService;

    @AutoLog(value = "生产日报-excel导出")
    @ApiOperation(value="生产日报-excel导出", notes="生产日报-excel导出")
    @GetMapping(value = "/getFaultProduceReportExportZip")
    public void getFaultProduceReportExportZip(FaultProduceReport faultProduceReport,
                                               String beginDay, String endDay){

    }
    @AutoLog(value = "物资信息-应急物资检查记录列表-excel导出")
    @ApiOperation(value="物资信息-应急物资检查记录列表-excel导出", notes="物资信息-应急物资检查记录列表-excel导出")
    @GetMapping(value = "/getFaultProduceReportExportExcel")
    public void getFaultProduceReportExportExcel(FaultProduceReportDTO faultProduceReport) throws IOException {
        faultProduceReportExportService.getFaultProduceReportExportExcel(faultProduceReport);
    }

}
