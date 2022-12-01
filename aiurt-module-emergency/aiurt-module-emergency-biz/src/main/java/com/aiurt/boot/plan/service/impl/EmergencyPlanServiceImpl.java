package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.*;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanServiceImpl extends ServiceImpl<EmergencyPlanMapper, EmergencyPlan> implements IEmergencyPlanService {
    @Autowired
    private IEmergencyPlanTeamService emergencyPlanTeamService;
    @Autowired
    private IEmergencyPlanMaterialsService emergencyPlanMaterialsService;
    @Autowired
    private IEmergencyPlanDisposalProcedureService emergencyPlanDisposalProcedureService;
    @Autowired
    private IEmergencyPlanAttService emergencyPlanAttService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveAndAdd(EmergencyPlanDTO emergencyPlanDto) {
        //应急预案添加
        //获取当前登录人组织部门作为编制部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgId = loginUser.getOrgId();

        EmergencyPlan emergencyPlan = new EmergencyPlan();
        BeanUtils.copyProperties(emergencyPlanDto, emergencyPlan);
        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
        emergencyPlan.setOrgCode(orgId);
        Double version =1.0;
        emergencyPlan.setEmergencyPlanVersion(String.valueOf(version));
        this.save(emergencyPlan);

        String id = emergencyPlan.getId();
        //应急队伍关联
        List<String> emergencyTeamId = emergencyPlanDto.getEmergencyTeamId();
        if(CollUtil.isNotEmpty(emergencyTeamId)){
            for (String s : emergencyTeamId) {
                EmergencyPlanTeam emergencyPlanTeam = new EmergencyPlanTeam();
                emergencyPlanTeam.setEmergencyTeamId(s);
                emergencyPlanTeam.setEmergencyPlanId(id);
                emergencyPlanTeamService.save(emergencyPlanTeam);
            }
        }
        //应急预案处置程序添加
        List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedure = emergencyPlanDto.getEmergencyPlanDisposalProcedure();
        if(CollUtil.isNotEmpty(emergencyPlanDisposalProcedure)){
            for (EmergencyPlanDisposalProcedure planDisposalProcedure : emergencyPlanDisposalProcedure) {
                planDisposalProcedure.setEmergencyPlanId(id);
                emergencyPlanDisposalProcedureService.save(planDisposalProcedure);
            }
        }
        //应急物资添加
        List<EmergencyPlanMaterials> emergencyPlanMaterials = emergencyPlanDto.getEmergencyPlanMaterials();
        if(CollUtil.isNotEmpty(emergencyPlanMaterials)){
            for (EmergencyPlanMaterials emergencyPlanMaterial : emergencyPlanMaterials) {
                emergencyPlanMaterial.setEmergencyPlanId(id);
                emergencyPlanMaterialsService.save(emergencyPlanMaterial);
            }
        }
        //应急预案附件添加
        List<EmergencyPlanAtt> emergencyPlanAtt = emergencyPlanDto.getEmergencyPlanAtt();
        if(CollUtil.isNotEmpty(emergencyPlanAtt)){
            for (EmergencyPlanAtt planAtt : emergencyPlanAtt) {
                planAtt.setEmergencyPlanId(id);
                emergencyPlanAttService.save(planAtt);
            }
        }
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String edit(EmergencyPlanDTO emergencyPlanDto) {
        String id = emergencyPlanDto.getId();

        Assert.notNull(id, "记录ID为空！");
        EmergencyPlan emPlan = this.getById(id);
        Assert.notNull(emPlan, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyPlanConstant.TO_SUBMITTED.equals(emPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("已提审的计划不允许编辑！");
        }
        EmergencyPlan emergencyPlan = new EmergencyPlan();
        BeanUtils.copyProperties(emergencyPlanDto, emergencyPlan);
        this.updateById(emergencyPlan);

        //应急队伍关联
        QueryWrapper<EmergencyPlanTeam> planTeamWrapper = new QueryWrapper<>();
        planTeamWrapper.lambda().eq(EmergencyPlanTeam::getEmergencyPlanId, id);
        emergencyPlanTeamService.remove(planTeamWrapper);
        List<String> emergencyTeamId = emergencyPlanDto.getEmergencyTeamId();
        if(CollUtil.isNotEmpty(emergencyTeamId)){
            for (String s : emergencyTeamId) {
                EmergencyPlanTeam emergencyPlanTeam = new EmergencyPlanTeam();
                emergencyPlanTeam.setEmergencyTeamId(s);
                emergencyPlanTeam.setEmergencyPlanId(id);
                emergencyPlanTeamService.save(emergencyPlanTeam);
            }
        }
        //应急预案处置程序编辑
        QueryWrapper<EmergencyPlanDisposalProcedure> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId, id);
        emergencyPlanDisposalProcedureService.remove(wrapper);
        List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedure = emergencyPlanDto.getEmergencyPlanDisposalProcedure();
        if (CollectionUtil.isNotEmpty(emergencyPlanDisposalProcedure)) {
            emergencyPlanDisposalProcedure.forEach(l -> {
                l.setEmergencyPlanId(emergencyPlan.getId());
            });
            emergencyPlanDisposalProcedureService.saveBatch(emergencyPlanDisposalProcedure);
        }
        //应急物资
        QueryWrapper<EmergencyPlanMaterials> planMaterialsWrapper = new QueryWrapper<>();
        planMaterialsWrapper.lambda().eq(EmergencyPlanMaterials::getEmergencyPlanId, id);
        emergencyPlanMaterialsService.remove(planMaterialsWrapper);
        List<EmergencyPlanMaterials> emergencyPlanMaterials = emergencyPlanDto.getEmergencyPlanMaterials();
        if (CollectionUtil.isNotEmpty(emergencyPlanMaterials)) {
            emergencyPlanMaterials.forEach(l -> {
                l.setEmergencyPlanId(emergencyPlan.getId());
            });
            emergencyPlanMaterialsService.saveBatch(emergencyPlanMaterials);
        }
        //应急预案附件
        QueryWrapper<EmergencyPlanAtt> planAttWrapper = new QueryWrapper<>();
        planAttWrapper.lambda().eq(EmergencyPlanAtt::getEmergencyPlanId,id);
        emergencyPlanAttService.remove(planAttWrapper);
        List<EmergencyPlanAtt> emergencyPlanAtt = emergencyPlanDto.getEmergencyPlanAtt();
        if (CollectionUtil.isNotEmpty(emergencyPlanAtt)) {
            emergencyPlanAtt.forEach(l -> {
                l.setEmergencyPlanId(emergencyPlan.getId());
            });
            emergencyPlanAttService.saveBatch(emergencyPlanAtt);
        }
        return id;
    }

    @Override
    public String change(EmergencyPlanDTO emergencyPlanDto) {
        String id = emergencyPlanDto.getId();

        Assert.notNull(id, "记录ID为空！");
        EmergencyPlan emPlan = this.getById(id);
        Assert.notNull(emPlan, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyPlanConstant.PASSED.equals(emPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("未审核通过的预案不能变更！");
        }
        EmergencyPlan emergencyPlan = new EmergencyPlan();
        BeanUtils.copyProperties(emergencyPlanDto, emergencyPlan);
        //获取部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgId = loginUser.getOrgId();

        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
        emergencyPlan.setOrgCode(orgId);
        emergencyPlan.setId(null);
        emergencyPlan.setEmergencyPlanVersion(String.valueOf(Double.valueOf(emergencyPlan.getEmergencyPlanVersion())+1));
        this.save(emergencyPlan);

        String newId = emergencyPlan.getId();
        //应急队伍关联
        List<String> emergencyTeamId = emergencyPlanDto.getEmergencyTeamId();
        if(CollUtil.isNotEmpty(emergencyTeamId)){
            for (String s : emergencyTeamId) {
                EmergencyPlanTeam emergencyPlanTeam = new EmergencyPlanTeam();
                emergencyPlanTeam.setEmergencyTeamId(s);
                emergencyPlanTeam.setEmergencyPlanId(newId);
                emergencyPlanTeamService.save(emergencyPlanTeam);
            }
        }
        //应急预案处置程序添加
        List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedure = emergencyPlanDto.getEmergencyPlanDisposalProcedure();
        if(CollUtil.isNotEmpty(emergencyPlanDisposalProcedure)){
            for (EmergencyPlanDisposalProcedure planDisposalProcedure : emergencyPlanDisposalProcedure) {
                planDisposalProcedure.setEmergencyPlanId(newId);
                emergencyPlanDisposalProcedureService.save(planDisposalProcedure);
            }
        }
        //应急物资添加
        List<EmergencyPlanMaterials> emergencyPlanMaterials = emergencyPlanDto.getEmergencyPlanMaterials();
        if(CollUtil.isNotEmpty(emergencyPlanMaterials)){
            for (EmergencyPlanMaterials emergencyPlanMaterial : emergencyPlanMaterials) {
                emergencyPlanMaterial.setEmergencyPlanId(newId);
                emergencyPlanMaterialsService.save(emergencyPlanMaterial);
            }
        }
        //应急预案附件添加
        List<EmergencyPlanAtt> emergencyPlanAtt = emergencyPlanDto.getEmergencyPlanAtt();
        if(CollUtil.isNotEmpty(emergencyPlanAtt)){
            for (EmergencyPlanAtt planAtt : emergencyPlanAtt) {
                planAtt.setEmergencyPlanId(newId);
                emergencyPlanAttService.save(planAtt);
            }
        }
        return newId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        EmergencyPlan emergencyPlan = this.getById(id);
        Assert.notNull(emergencyPlan, "未找到对应数据！");
        // 非待提审状态不允许删除
        if (!EmergencyPlanConstant.TO_SUBMITTED.equals(emergencyPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("已提审的计划不允许删除！");
        }
        this.removeById(id);

        //关联应急队伍删除
        QueryWrapper<EmergencyPlanTeam> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyPlanTeam::getEmergencyPlanId, id);
        emergencyPlanTeamService.remove(wrapper);
        //关联应急预案处置程序删除
        QueryWrapper<EmergencyPlanDisposalProcedure> planDisposalProcedureWrapper = new QueryWrapper<>();
        planDisposalProcedureWrapper.lambda().eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId, id);
        emergencyPlanDisposalProcedureService.remove(planDisposalProcedureWrapper);
        //关联应急物资删除
        QueryWrapper<EmergencyPlanMaterials> planMaterialsWrapper = new QueryWrapper<>();
        planMaterialsWrapper.lambda().eq(EmergencyPlanMaterials::getEmergencyPlanId, id);
        emergencyPlanMaterialsService.remove(planMaterialsWrapper);
        //关联应急预案附件删除
        QueryWrapper<EmergencyPlanAtt> planAttWrapper = new QueryWrapper<>();
        planAttWrapper.lambda().eq(EmergencyPlanAtt::getEmergencyPlanId, id);
        emergencyPlanAttService.remove(planAttWrapper);
    }

    @Override
    public String startProcess(String id) {
        EmergencyPlan emergencyPlan = this.getById(id);
        Assert.notNull(emergencyPlan, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyPlanConstant.TO_SUBMITTED.equals(emergencyPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("该计划已经提审，无需重复提审！");
        }
        //待提审改成待审核
        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_REVIEWED);
        this.updateById(emergencyPlan);
        return id;
    }

    @Override
    public String openOrStop(String id) {
        EmergencyPlan emergencyPlan = this.getById(id);
        Assert.notNull(emergencyPlan, "未找到对应数据！");
        // 审核通过才能启用和停用
        if (!EmergencyPlanConstant.PASSED.equals(emergencyPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("该计划还未审核通过，不能启用和停用");
        }
        //审核通过并且已停用改成有效
        if(EmergencyPlanConstant.PASSED.equals(emergencyPlan.getEmergencyPlanStatus()) & EmergencyPlanConstant.STOPPED.equals(emergencyPlan.getStatus())){
            emergencyPlan.setStatus(EmergencyPlanConstant.VALID);
            this.updateById(emergencyPlan);
        }
        //审核通过并且有效改成已停用
        if(EmergencyPlanConstant.PASSED.equals(emergencyPlan.getEmergencyPlanStatus()) & EmergencyPlanConstant.VALID.equals(emergencyPlan.getStatus())){
            emergencyPlan.setStatus(EmergencyPlanConstant.STOPPED);
            this.updateById(emergencyPlan);
        }
        if(EmergencyPlanConstant.PASSED.equals(emergencyPlan.getEmergencyPlanStatus()) & "0".equals(emergencyPlan.getStatus())){
            emergencyPlan.setStatus(EmergencyPlanConstant.VALID);
            this.updateById(emergencyPlan);
        }
        return id;
    }

    @Override
    public void audit(String id) {

    }

    @Override
    public List<EmergencyPlanDTO> getPlanInfo(String id) {

        return null;
    }
}
