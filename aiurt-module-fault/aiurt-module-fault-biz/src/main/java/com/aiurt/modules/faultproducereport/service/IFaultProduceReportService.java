package com.aiurt.modules.faultproducereport.service;

import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
public interface IFaultProduceReportService extends IService<FaultProduceReport> {


    Result<IPage<FaultProduceReportDTO>> queryPageList(Page<FaultProduceReportDTO> pageList, FaultProduceReport faultProduceReport,
                                                       String beginDay, String endDay);

    Result<IPage<FaultProduceReportDTO>> queryPageAuditList(Page<FaultProduceReportDTO> pageList, FaultProduceReport faultProduceReport,
                                                    String beginDay, String endDay);

    void workSubmit(FaultProduceReport faultProduceReport);

    void exportZip(FaultProduceReportDTO faultProduceReportDTO, HttpServletRequest request, HttpServletResponse response) throws IOException;

    void exportExcel(FaultProduceReportDTO faultProduceReportDTO, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
