package com.aiurt.modules.faultproducereport.service;

import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;

import java.io.IOException;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
public interface IFaultProduceReportExportService {


    void getFaultProduceReportExportExcel(FaultProduceReportDTO faultProduceReport) throws IOException;
}
