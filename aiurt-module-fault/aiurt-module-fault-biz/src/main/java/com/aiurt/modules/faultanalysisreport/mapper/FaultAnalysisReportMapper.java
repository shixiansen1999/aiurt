package com.aiurt.modules.faultanalysisreport.mapper;

import java.util.List;

import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
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
     * @param ids
     * @return List<FaultAnalysisReport>
     * */
    List<FaultAnalysisReport> readAll(@Param("page")Page<FaultAnalysisReport> page, @Param("condition")FaultAnalysisReport condition,@Param("ids")List<String> ids);

    /**
     * 故障选择查询
     * @param page
     * @param condition
     * @param allSubSystem
     * @param faultCodes
     * @return List<Fault>
     * */
    List<FaultDTO> getFault(@Param("page")Page<FaultDTO> page, @Param("condition")FaultDTO condition,
                            @Param("allSubSystem")List<String> allSubSystem,@Param("faultCodes")List<String> faultCodes);

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
