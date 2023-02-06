package com.aiurt.boot.materials.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsCategoryMapper;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsUsageMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.aiurt.common.api.CommonAPI;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private EmergencyMaterialsCategoryMapper emergencyMaterialsCategoryMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;

    @Lazy
    @Autowired
    private CommonAPI api;

    @Override
    public Page<EmergencyMaterialsUsage> getUsageRecordList(Page<EmergencyMaterialsUsage> pageList, EmergencyMaterialsUsage condition) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> departByUserId = api.getDepartByUserId(sysUser.getId());
        if(StrUtil.isBlank(condition.getPrimaryOrg()) && CollectionUtil.isNotEmpty(departByUserId)){
            List<String> collect = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect)){
                condition.setPrimaryCodeList(collect);
            }
        }
        if(StrUtil.isNotBlank(condition.getPrimaryOrg())){
            //根据编码查询部门信息
            SysDepartModel departByOrgCode = iSysBaseAPI.getDepartByOrgCode(condition.getPrimaryOrg());
            if (ObjectUtil.isNotEmpty(departByOrgCode)){
                //查询子级信息
                List<SysDepartModel> departByParentId = iSysBaseAPI.getDepartByParentId(departByOrgCode.getId());
                if(CollectionUtil.isNotEmpty(departByUserId) && CollectionUtil.isNotEmpty(departByParentId)){

                    List<String> collect = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());

                    List<String> collect1 = departByParentId.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
                    if (collect1.size()>collect.size()){
                        collect1.add(condition.getPrimaryOrg());
                        collect1.retainAll(collect);
                        condition.setPrimaryCodeList(collect1);
                    }
                    if (collect.size()>collect1.size()){
                        collect1.add(condition.getPrimaryOrg());
                        collect.retainAll(collect1);
                        condition.setPrimaryCodeList(collect);
                    }
                }else {
                    List<String> stringList = new ArrayList<>();
                    stringList.add(condition.getPrimaryOrg());
                    condition.setPrimaryCodeList(stringList);
                }
            }else {
                List<String> collect = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)){
                    condition.setPrimaryCodeList(collect);
                }
            }
        }
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
            if (StrUtil.isNotBlank(e.getCategoryCode())){
                LambdaQueryWrapper<EmergencyMaterialsCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(EmergencyMaterialsCategory::getDelFlag,0)
                                  .eq(EmergencyMaterialsCategory::getCategoryCode,e.getCategoryCode());
                EmergencyMaterialsCategory emergencyMaterialsCategory = emergencyMaterialsCategoryMapper.selectOne(lambdaQueryWrapper);
                if (StrUtil.isNotBlank(emergencyMaterialsCategory.getCategoryName())){
                    e.setCategoryName(emergencyMaterialsCategory.getCategoryName());
                }
            }
        });
        return pageList.setRecords(usageRecordList);
    }
}
