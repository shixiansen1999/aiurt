package com.aiurt.modules.faultanalysisreport.service.impl;

import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class FaultAnalysisReportServiceImpl extends ServiceImpl<FaultAnalysisReportMapper, FaultAnalysisReport> implements IFaultAnalysisReportService {

    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;

    @Override
    public IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport) {
        List<FaultAnalysisReport> faultAnalysisReports = faultAnalysisReportMapper.readAll(page, faultAnalysisReport);
        return page.setRecords(faultAnalysisReports);
    }
}
