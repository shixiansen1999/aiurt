package com.aiurt.boot.materials.service;

import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.MaterialPatrolDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsService extends IService<EmergencyMaterials> {


    /**
     * 应急物资台账列表
     * @param pageList
     * @param condition
     * @return
     */
    Page<MaterialAccountDTO> getMaterialAccountList(Page<MaterialAccountDTO> pageList, MaterialAccountDTO condition);

    /**
     * 应急物资巡检登记
     * @return
     */
    MaterialPatrolDTO getMaterialPatrol();
}
