package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.vo.EmergencyPlanRecordVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyPlanMapper extends BaseMapper<EmergencyPlan> {

    IPage<EmergencyPlan> queryPageList(@Param("page") Page<EmergencyPlan> page
            , @Param("condition") EmergencyPlanQueryDTO emergencyPlanQueryDTO, @Param("orgCodes") List<String> orgCodes);

    /**
     * 按条件查询应急预案
     * @param emergencyPlanDto
     * @return
     */
    List<EmergencyPlanExcelDTO> selectListNoPage(@Param("condition") EmergencyPlanDTO emergencyPlanDto);

    /**
     * 按条件查询预案处置程序
     * @param id
     * @return
     */
    List<EmergencyPlanDisposalProcedureExcelDTO> selectPlanDisposalProcedureById(String id);

    List<EmergencyPlanMaterialsExcelDTO> selectPlanMaterialsById(String id);

}
