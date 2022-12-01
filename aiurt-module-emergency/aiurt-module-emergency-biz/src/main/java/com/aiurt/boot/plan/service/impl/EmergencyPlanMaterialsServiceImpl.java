package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsMapper;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
import com.aiurt.boot.plan.mapper.EmergencyPlanMaterialsMapper;
import com.aiurt.boot.plan.service.IEmergencyPlanMaterialsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_plan_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanMaterialsServiceImpl extends ServiceImpl<EmergencyPlanMaterialsMapper, EmergencyPlanMaterials> implements IEmergencyPlanMaterialsService {
    @Autowired
    private EmergencyPlanMaterialsMapper emergencyPlanMaterialsMapper;

    @Override
    public Page<EmergencyPlanMaterialsDTO> getMaterialAccountList(Page<EmergencyPlanMaterialsDTO> pageList, EmergencyPlanMaterialsDTO condition) {
        List<EmergencyPlanMaterialsDTO> planMaterialsList = emergencyPlanMaterialsMapper.getMaterialAccountList(pageList, condition);
        return pageList.setRecords(planMaterialsList);
    }
}
