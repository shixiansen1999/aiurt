package com.aiurt.boot.materials.service.impl;

import com.aiurt.boot.materials.mapper.EmergencyMaterialsUsageMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsUsageServiceImpl extends ServiceImpl<EmergencyMaterialsUsageMapper, EmergencyMaterialsUsage> implements IEmergencyMaterialsUsageService {

    @Autowired
    private EmergencyMaterialsUsageMapper emergencyMaterialsUsageMapper;

    @Override
    public Page<EmergencyMaterialsUsage> getUsageRecordList(Page<EmergencyMaterialsUsage> pageList, EmergencyMaterialsUsage condition) {
        List<EmergencyMaterialsUsage> usageRecordList = emergencyMaterialsUsageMapper.getUsageRecordList(pageList, condition);
        return pageList.setRecords(usageRecordList);
    }
}
