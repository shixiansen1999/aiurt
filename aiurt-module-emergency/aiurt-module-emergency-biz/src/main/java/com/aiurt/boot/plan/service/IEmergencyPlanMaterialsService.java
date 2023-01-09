package com.aiurt.boot.plan.service;

import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanMaterialsService extends IService<EmergencyPlanMaterials> {
    /**
     * 应急预案物资列表查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<EmergencyPlanMaterialsDTO> getMaterialAccountList(Page<EmergencyPlanMaterialsDTO> pageList, EmergencyPlanMaterialsDTO condition);

    /**
     * 根据应急预案id查询物资信息
     * @param id
     * @return
     */
    List<EmergencyPlanMaterialsDTO> queryById(String id);

}
