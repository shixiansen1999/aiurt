package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesItemMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsInvoicesItemServiceImpl extends ServiceImpl<EmergencyMaterialsInvoicesItemMapper, EmergencyMaterialsInvoicesItem> implements IEmergencyMaterialsInvoicesItemService {
    @Autowired
    private EmergencyMaterialsInvoicesItemMapper emergencyMaterialsInvoicesItemMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;


    @Override
    public List<EmergencyMaterialsInvoicesItem> getPatrolRecord(String materialsCode, String startTime, String endTime) {
        List<EmergencyMaterialsInvoicesItem> patrolRecord = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(materialsCode, startTime, endTime);
        patrolRecord.forEach(e->{
            if (StrUtil.isNotBlank(e.getPatrolId())){
                //根据巡检人id查询巡检人信息
                LoginUser userById = iSysBaseAPI.getUserById(e.getPatrolId());
                e.setPatrolName(userById.getRealname());
                e.setPatrolTeam(userById.getOrgName());
            }
        });
        return patrolRecord;
    }
}
