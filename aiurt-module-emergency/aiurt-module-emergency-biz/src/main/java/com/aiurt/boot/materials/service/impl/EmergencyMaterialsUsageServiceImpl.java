package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsUsageMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
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

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Override
    public Page<EmergencyMaterialsUsage> getUsageRecordList(Page<EmergencyMaterialsUsage> pageList, EmergencyMaterialsUsage condition) {
        List<EmergencyMaterialsUsage> usageRecordList = emergencyMaterialsUsageMapper.getUsageRecordList(pageList, condition);
        usageRecordList.forEach(e->{
            //根据使用人ID查询使用人名称
            if (StrUtil.isNotBlank(e.getUserId())){
                LoginUser userById = iSysBaseAPI.getUserById(e.getUserId());
                e.setUserName(userById.getRealname());
            }
            //根据归还人ID查询归还人名称
            if (StrUtil.isNotBlank(e.getBackId())){
                LoginUser userById = iSysBaseAPI.getUserById(e.getBackId());
                e.setBackName(userById.getRealname());
            }
        });
        return pageList.setRecords(usageRecordList);
    }
}
