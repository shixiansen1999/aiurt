package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.collection.CollUtil;
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
import java.util.stream.Collectors;

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
    public Page<EmergencyMaterialsInvoicesItem> getPatrolRecord(Page<EmergencyMaterialsInvoicesItem> pageList, String materialsCode, String startTime, String endTime,String  standardCode) {
        //父级
        List<EmergencyMaterialsInvoicesItem> patrolRecord = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList,materialsCode, startTime, endTime,standardCode,"0");
        patrolRecord.forEach(e->{
            if (StrUtil.isNotBlank(e.getPatrolId())) {
                //根据巡视人id查询巡视人名称
                String[] split = e.getPatrolId().split(",");
                List<LoginUser> loginUsers = iSysBaseAPI.queryAllUserByIds(split);
                if (CollUtil.isNotEmpty(loginUsers)){
                    String collect = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    e.setPatrolName(collect);
                }
            }if(StrUtil.isNotBlank(e.getPatrolTeamCode())){
                //根据巡检班组code查询巡检班组名称
                String departNameByOrgCode = iSysBaseAPI.getDepartNameByOrgCode(e.getPatrolTeamCode());
                e.setPatrolTeamName(departNameByOrgCode);
            }if("0".equals(e.getPid()) && StrUtil.isNotBlank(e.getId())){
                List<EmergencyMaterialsInvoicesItem> patrolRecord1 = emergencyMaterialsInvoicesItemMapper.getPatrolRecord(pageList, materialsCode, startTime, endTime, standardCode, e.getId());
                e.setSubLevel(patrolRecord1);
            }
        });
        return pageList.setRecords(patrolRecord);
    }
}
