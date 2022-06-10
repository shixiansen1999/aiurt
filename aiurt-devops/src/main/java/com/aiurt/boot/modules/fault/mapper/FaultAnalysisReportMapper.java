package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.result.FaultAnalysisReportResult;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.modules.fault.entity.FaultAnalysisReport;
import com.swsc.copsms.modules.fault.param.FaultAnalysisReportParam;
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
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<FaultAnalysisReportResult> queryFaultAnalysisReport(IPage<FaultAnalysisReportResult> page, Wrapper<FaultAnalysisReportResult> queryWrapper,
                                                              @Param("param") FaultAnalysisReportParam param);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    int deleteOne(@Param("id") Integer id);

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    FaultAnalysisReportResult selectAnalysisReport(String code);

    /**
     * 根据code查询故障分析报告
     * @param code
     * @return
     */
    FaultAnalysisReportResult selectLastOne(String code);

}
