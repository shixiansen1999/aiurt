package com.aiurt.modules.faultanalysisreport.mapper;

import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


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
     * @param ids
     * @return List<FaultAnalysisReport>
     * */
    List<FaultAnalysisReport> readAll(@Param("page")Page<FaultAnalysisReport> page, @Param("condition")FaultAnalysisReport condition,@Param("ids")List<String> ids,@Param("userName")String userName);

    List<FaultAnalysisReport> readAll2(@Param("page")Page<FaultAnalysisReport> page, @Param("condition")FaultAnalysisReport condition, @Param("reportList")List<FaultAnalysisReport> reportList, @Param("userName")String userName);

    /**
     * 查询已经被引用的故障
     * @return List<String>
     * */
    List<String> getFaultCode();

    /**
     * 提交中的故障分析的故障详情
     * @param id
     * @return FaultDTO
     */
    FaultDTO getDetail(@Param("id")String id);

    /**
     * 故障分析通过id查询详情
     * @param id
     * @param faultCode
     * @return IPage<FaultAnalysisReport>
     */
    FaultAnalysisReport readOne(@Param("id")String id,@Param("faultCode")String faultCode);

}
