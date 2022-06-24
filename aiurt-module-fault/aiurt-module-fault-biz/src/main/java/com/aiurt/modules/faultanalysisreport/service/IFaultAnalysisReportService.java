package com.aiurt.modules.faultanalysisreport.service;

import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface IFaultAnalysisReportService extends IService<FaultAnalysisReport> {
    /**
     * 故障分析查询
     * @param page
     * @param faultAnalysisReport
     * @return IPage<FaultAnalysisReport>
     */
    public IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport);

}
