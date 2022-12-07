package com.aiurt.boot.plan.mapper;

import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecordMaterials;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_plan_record_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyPlanRecordMaterialsMapper extends BaseMapper<EmergencyPlanRecordMaterials> {
    /**
     * 应急预案物资列表查询
     * @param pageList
     * @param condition
     * @return
     */
    List<EmergencyPlanMaterialsDTO> getMaterialAccountList(@Param("pageList") Page<EmergencyPlanMaterialsDTO> pageList, @Param("condition") EmergencyPlanMaterialsDTO condition);

}
