package com.aiurt.modules.faultanalysisreport.service;

import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.entity.dto.FaultDTO;
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
     IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport);

    /**
     * 故障分析通过id查询详情
     * @param id
     * @return IPage<FaultAnalysisReport>
     */
    FaultAnalysisReport readOne(String id,String faultCode);

    /**
     * 故障选择查询
     * @param page
     * @param faultDTO
     * @return List<Fault>
     * */
     IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO);

    /**
     * 提交中的故障分析的故障详情
     * @param id
     * @return FaultDTO
     */
    FaultDTO getDetail(String id);



}
