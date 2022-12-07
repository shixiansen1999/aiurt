package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecordMaterials;
import com.aiurt.boot.plan.mapper.EmergencyPlanMaterialsMapper;
import com.aiurt.boot.plan.mapper.EmergencyPlanRecordMaterialsMapper;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordMaterialsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_plan_record_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanRecordMaterialsServiceImpl extends ServiceImpl<EmergencyPlanRecordMaterialsMapper, EmergencyPlanRecordMaterials> implements IEmergencyPlanRecordMaterialsService {
    @Autowired
    private EmergencyPlanRecordMaterialsMapper emergencyPlanRecordMaterialsMapper;
    @Override
    public Page<EmergencyPlanMaterialsDTO> getMaterialAccountList(Page<EmergencyPlanMaterialsDTO> pageList, EmergencyPlanMaterialsDTO condition) {
        List<EmergencyPlanMaterialsDTO> planMaterialsList = emergencyPlanRecordMaterialsMapper.getMaterialAccountList(pageList, condition);
        return pageList.setRecords(planMaterialsList);
    }
}
