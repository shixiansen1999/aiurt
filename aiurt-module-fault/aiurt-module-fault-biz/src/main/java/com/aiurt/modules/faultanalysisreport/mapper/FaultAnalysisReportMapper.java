package com.aiurt.modules.faultanalysisreport.mapper;

import java.util.List;

import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface FaultAnalysisReportMapper extends BaseMapper<FaultAnalysisReport> {


    /**
     * 查询全部故障分析
     * @param page
     * @param condition
     * @return List<FaultAnalysisReport>
     * */
    List<FaultAnalysisReport> readAll(@Param("page")Page<FaultAnalysisReport> page, @Param("condition")FaultAnalysisReport condition);

    /**
     * 故障选择查询
     * @param page
     * @param condition
     * @return List<Fault>
     * */
    List<Fault> getFault(@Param("page")Page<Fault> page, @Param("condition")Fault condition);


}
