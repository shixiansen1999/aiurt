package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesItemMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesItemService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public Page<EmergencyMaterialsInvoicesItem> getPatrolRecord(Page<EmergencyMaterialsInvoicesItem> pageList, String materialsCode, String startTime, String endTime) {
        List<EmergencyMaterialsInvoicesItem> patrolRecord = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList,materialsCode, startTime, endTime);
        patrolRecord.forEach(e->{
            if (StrUtil.isNotBlank(e.getPatrolId())){
                //根据巡检人id查询巡检名称
                LoginUser userById = iSysBaseAPI.getUserById(e.getPatrolId());
                e.setPatrolName(userById.getRealname());
            }if(StrUtil.isNotBlank(e.getPatrolTeamCode())){
                //根据巡检班组code查询巡检班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getPatrolTeamCode());
                e.setPatrolTeamName(departNameByOrgCode);
            }
        });
        return pageList.setRecords(patrolRecord);
    }
}
