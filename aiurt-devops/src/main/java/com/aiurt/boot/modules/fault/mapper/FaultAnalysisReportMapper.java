package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.common.result.FaultAnalysisReportResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.fault.entity.FaultAnalysisReport;
import com.aiurt.boot.modules.fault.param.FaultAnalysisReportParam;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 故障分析报告
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultAnalysisReportMapper extends BaseMapper<FaultAnalysisReport> {
    /**
     * 查询故障分析报告
     * @param page
     * @param param
     * @return
     */
    IPage<FaultAnalysisReportResult> queryFaultAnalysisReport(IPage<FaultAnalysisReportResult> page,
                                                              @Param("param") FaultAnalysisReportParam param);

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    FaultAnalysisReportResult selectAnalysisReport(String code);

}
