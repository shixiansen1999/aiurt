package com.aiurt.boot.modules.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.common.result.FaultAnalysisReportResult;
import com.aiurt.boot.modules.fault.dto.FaultAnalysisReportDTO;
import com.aiurt.boot.modules.fault.entity.FaultAnalysisReport;
import com.aiurt.boot.modules.fault.param.FaultAnalysisReportParam;

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
     * @param param
     * @return
     */
    IPage<FaultAnalysisReportResult> pageList(IPage<FaultAnalysisReportResult> page, FaultAnalysisReportParam param);

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
    Result<?> add(FaultAnalysisReportDTO dto, HttpServletRequest req);

}
