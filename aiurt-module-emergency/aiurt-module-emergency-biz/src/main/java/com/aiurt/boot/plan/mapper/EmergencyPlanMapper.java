package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.plan.vo.EmergencyPlanExportExcelVO;
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
    /**
     * 应急预案分页查询
     * @param page
     * @param emergencyPlanQueryDTO
     * @param orgCodes
     * @return
     */
    IPage<EmergencyPlan> queryPageList(@Param("page") Page<EmergencyPlan> page,
                                       @Param("condition") EmergencyPlanQueryDTO emergencyPlanQueryDTO,
                                       @Param("orgCodes") List<String> orgCodes);

    /**
     * 应急预案分页审核
     * @param page
     * @param emergencyPlanQueryDTO
     * @param orgCodes
     * @return
     */
    IPage<EmergencyPlan> queryWorkToDo(@Param("page") Page<EmergencyPlan> page,
                                       @Param("condition") EmergencyPlanQueryDTO emergencyPlanQueryDTO,
                                       @Param("orgCodes") List<String> orgCodes,
                                       @Param("userName") String userName);

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

    /**
     * 按条件查询应急预案物资
     * @param id
     * @return
     */
    List<EmergencyPlanMaterialsExcelDTO> selectPlanMaterialsById(String id);

    /**
     * 根据部门名称查找orgcode
     * @param orgName
     * @return
     */
    String selectDepartCode(String orgName);

    /**
     * 根据角色名称查找roleId
     * @param roleName
     * @return
     */
    String selectRoleId(String roleName);

   EmergencyPlanExportExcelVO queryById(String id);

}
