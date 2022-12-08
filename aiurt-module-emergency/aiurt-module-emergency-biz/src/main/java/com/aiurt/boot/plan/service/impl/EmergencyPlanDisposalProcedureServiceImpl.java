package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.boot.plan.entity.EmergencyPlanRecordDisposalProcedure;
import com.aiurt.boot.plan.mapper.EmergencyPlanDisposalProcedureMapper;
import com.aiurt.boot.plan.service.IEmergencyPlanDisposalProcedureService;
import com.aiurt.boot.plan.service.IEmergencyPlanRecordDisposalProcedureService;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: emergency_plan_disposal_procedure
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanDisposalProcedureServiceImpl extends ServiceImpl<EmergencyPlanDisposalProcedureMapper, EmergencyPlanDisposalProcedure> implements IEmergencyPlanDisposalProcedureService {
    @Autowired
    @Lazy
    private IEmergencyPlanDisposalProcedureService emergencyPlanDisposalProcedureService;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Override
    public List<EmergencyPlanDisposalProcedure> queryById(String id) {
        List<EmergencyPlanDisposalProcedure> procedureList = emergencyPlanDisposalProcedureService.lambdaQuery()
                .eq(EmergencyPlanDisposalProcedure::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId, id).list();
        this.disposalProcedureTranslate(procedureList);

        return procedureList;
    }

    /**
     * 关联的问题列表的字典，组织机构名称转换
     *
     * @param procedureList
     */
    private void disposalProcedureTranslate(List<EmergencyPlanDisposalProcedure> procedureList) {
        if (CollectionUtil.isNotEmpty(procedureList)) {
            Map<String, String> orgMap = sysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            Map<String, String> roleMap = sysBaseApi.queryAllRole().stream()
                    .collect(Collectors.toMap(k -> k.getId(), v -> v.getTitle(), (a, b) -> a));

            procedureList.forEach(l -> {
                l.setOrgName(orgMap.get(String.valueOf(l.getOrgCode())));
                l.setRoleName(roleMap.get(String.valueOf(l.getRoleId())));
            });
        }
    }
}
