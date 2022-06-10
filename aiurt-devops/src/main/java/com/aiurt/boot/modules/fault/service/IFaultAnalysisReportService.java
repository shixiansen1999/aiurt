package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.common.result.FaultAnalysisReportResult;
import com.swsc.copsms.modules.fault.dto.FaultAnalysisReportDTO;
import com.swsc.copsms.modules.fault.entity.FaultAnalysisReport;
import com.swsc.copsms.modules.fault.param.FaultAnalysisReportParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 故障分析报告
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultAnalysisReportService extends IService<FaultAnalysisReport> {

    /**
     * 查询故障分析报告
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<FaultAnalysisReportResult> pageList(IPage<FaultAnalysisReportResult> page, Wrapper<FaultAnalysisReportResult> queryWrapper, FaultAnalysisReportParam param);

    /**
     * 根据id假删除
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    FaultAnalysisReportResult getAnalysisReport(String code);

    /**
     * 新增故障分析报告
     * @param dto
     */
    public void add(FaultAnalysisReportDTO dto, HttpServletRequest req);


}
