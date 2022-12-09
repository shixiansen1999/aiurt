package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanQueryDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDepartDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.rehearsal.constant.EmergencyConstant;
import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRecordQuestion;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.aiurt.boot.team.constant.TeamConstant;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.ComboModel;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanServiceImpl extends ServiceImpl<EmergencyPlanMapper, EmergencyPlan> implements IEmergencyPlanService, IFlowableBaseUpdateStatusService {
    @Autowired
    private IEmergencyPlanTeamService emergencyPlanTeamService;
    @Autowired
    private IEmergencyPlanMaterialsService emergencyPlanMaterialsService;
    @Autowired
    private IEmergencyPlanDisposalProcedureService emergencyPlanDisposalProcedureService;
    @Autowired
    private IEmergencyPlanAttService emergencyPlanAttService;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IEmergencyTeamService emergencyTeamService;
    @Autowired
    @Lazy
    private IEmergencyPlanService emergencyPlanService;
    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<EmergencyPlan> queryPageList(Page<EmergencyPlan> page, EmergencyPlanQueryDTO emergencyPlanQueryDto) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        List<CsUserDepartModel> deptModel = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> orgCodes = deptModel.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode()))
                .map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        IPage<EmergencyPlan> pageList = emergencyPlanMapper.queryPageList(page, emergencyPlanQueryDto,orgCodes);

        return pageList;

    }

    /**
     * 应急预案台账保存
     * @param emergencyPlanDto
     * @return
     */
    public String startProcess(EmergencyPlanDTO emergencyPlanDto){
        String id = emergencyPlanDto.getId();
        if (StrUtil.isEmpty(id)) {
            //应急预案添加
            //登录人组织部门作为编制部门
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String orgCode = loginUser.getOrgCode();

            EmergencyPlan emergencyPlan = new EmergencyPlan();
            BeanUtils.copyProperties(emergencyPlanDto, emergencyPlan);
            emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
            emergencyPlan.setStatus(null);
            emergencyPlan.setOrgCode(orgCode);
            Double version =1.0;
            emergencyPlan.setEmergencyPlanVersion(String.valueOf(version));
            this.save(emergencyPlan);

            String planId = emergencyPlan.getId();
            //应急队伍关联
            List<String> emergencyTeamId = emergencyPlanDto.getEmergencyTeamId();
            if(CollUtil.isNotEmpty(emergencyTeamId)){
                for (String s : emergencyTeamId) {
                    EmergencyPlanTeam emergencyPlanTeam = new EmergencyPlanTeam();
                    emergencyPlanTeam.setEmergencyTeamId(s);
                    emergencyPlanTeam.setEmergencyPlanId(planId);
                    emergencyPlanTeamService.save(emergencyPlanTeam);
                }
            }
            //应急预案处置程序添加
            List<EmergencyPlanDisposalProcedure> emergencyPlanDisposalProcedure = emergencyPlanDto.getEmergencyPlanDisposalProcedure();
            if(CollUtil.isNotEmpty(emergencyPlanDisposalProcedure)){
                for (EmergencyPlanDisposalProcedure planDisposalProcedure : emergencyPlanDisposalProcedure) {
                    planDisposalProcedure.setEmergencyPlanId(planId);
                    emergencyPlanDisposalProcedureService.save(planDisposalProcedure);
                }
            }
            //应急物资添加
            List<EmergencyPlanMaterials> emergencyPlanMaterials = emergencyPlanDto.getEmergencyPlanMaterials();
            if(CollUtil.isNotEmpty(emergencyPlanMaterials)){
                for (EmergencyPlanMaterials emergencyPlanMaterial : emergencyPlanMaterials) {
                    emergencyPlanMaterial.setEmergencyPlanId(planId);
                    emergencyPlanMaterialsService.save(emergencyPlanMaterial);
                }
            }
            //应急预案附件添加
            List<EmergencyPlanAtt> emergencyPlanAtt = emergencyPlanDto.getEmergencyPlanAtt();
            if(CollUtil.isNotEmpty(emergencyPlanAtt)){
                for (EmergencyPlanAtt planAtt : emergencyPlanAtt) {
                    planAtt.setEmergencyPlanId(planId);
                    emergencyPlanAttService.save(planAtt);
                }
            }
            return planId;
        }else{
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

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveAndAdd(EmergencyPlanDTO emergencyPlanDto) {
        //应急预案添加
        //登录人组织部门作为编制部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();

        EmergencyPlan emergencyPlan = new EmergencyPlan();
        BeanUtils.copyProperties(emergencyPlanDto, emergencyPlan);
        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
        emergencyPlan.setStatus(null);
        emergencyPlan.setOrgCode(orgCode);
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
    @Transactional(rollbackFor = Exception.class)
    public String change(EmergencyPlanDTO emergencyPlanDto) {
        String id = emergencyPlanDto.getId();

        Assert.notNull(id, "记录ID为空！");
        EmergencyPlan emPlan = this.getById(id);
        Assert.notNull(emPlan, "未找到对应数据！");
        // 审核通过才允许变更
        if (!EmergencyPlanConstant.PASSED.equals(emPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("未审核通过的预案不能变更！");
        }
        EmergencyPlan emergencyPlan = new EmergencyPlan();
        //获取部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        //创建新的应急预案
        emergencyPlan.setEmergencyPlanType(emergencyPlanDto.getEmergencyPlanType());
        emergencyPlan.setEmergencyPlanName(emergencyPlanDto.getEmergencyPlanName());
        emergencyPlan.setEmergencyPlanContent(emergencyPlanDto.getEmergencyPlanContent());
        emergencyPlan.setKeyWord(emergencyPlanDto.getKeyWord());
        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
        emergencyPlan.setOrgCode(orgCode);
        String emergencyPlanVersion = emergencyPlanDto.getEmergencyPlanVersion();
        emergencyPlan.setEmergencyPlanVersion(String.valueOf(Double.valueOf(emergencyPlanVersion)+1));
        emergencyPlan.setOldPlanId(emergencyPlanDto.getId());
        emergencyPlan.setStatus(null);
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
    @Transactional(rollbackFor = Exception.class)
    public String commit(String id) {
        EmergencyPlan emergencyPlan = this.getById(id);
        Assert.notNull(emergencyPlan, "未找到对应数据！");
        // 代提审才允许编辑
        if (!EmergencyPlanConstant.TO_SUBMITTED.equals(emergencyPlan.getEmergencyPlanStatus()) && !EmergencyPlanConstant.REJECTED.equals(emergencyPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("该计划已经提审，无需重复提审！");
        }
        //待提审改成待审核
        emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.TO_REVIEWED);
        this.updateById(emergencyPlan);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String openOrStop(String id) {
        EmergencyPlan emergencyPlan = this.getById(id);
        Assert.notNull(emergencyPlan, "未找到对应数据！");
        // 审核通过才能启用和停用
        if (!EmergencyPlanConstant.PASSED.equals(emergencyPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("该计划还未审核通过，不能启用和停用");
        }else{
            //审核通过并且预案状态为空改为启用
            if(ObjectUtil.isEmpty(emergencyPlan.getStatus())){
                emergencyPlan.setStatus(EmergencyPlanConstant.VALID);
                this.updateById(emergencyPlan);
            }
            //审核通过并且有效改成已停用
            else if(EmergencyPlanConstant.VALID.equals(emergencyPlan.getStatus())){
                emergencyPlan.setStatus(EmergencyPlanConstant.STOPPED);
                this.updateById(emergencyPlan);
            }
            //审核通过并且已停用改成有效
            else if(EmergencyPlanConstant.STOPPED.equals(emergencyPlan.getStatus())){
                emergencyPlan.setStatus(EmergencyPlanConstant.VALID);
                this.updateById(emergencyPlan);
            }
        }
        return id;
    }


    /**
     * 应急预案台账查看详情
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmergencyPlanDTO queryById(String id) {
        EmergencyPlan plan = this.getById(id);
        Assert.notNull(plan, "未找到对应记录！");
        EmergencyPlanDTO planDto = new EmergencyPlanDTO();
        BeanUtils.copyProperties(plan, planDto);

        // 获取应急队伍
        List<EmergencyPlanTeam> teamList = emergencyPlanTeamService.lambdaQuery()
                .eq(EmergencyPlanTeam::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanTeam::getEmergencyPlanId, id).list();
        List<String> teamName = new ArrayList<>();
        if(CollUtil.isNotEmpty(teamList)){
            for (EmergencyPlanTeam planTeam : teamList) {
                List<EmergencyTeam> list = emergencyTeamService.lambdaQuery().eq(EmergencyTeam::getId, planTeam.getEmergencyTeamId()).list();
                if(CollUtil.isNotEmpty(list)){
                    for (EmergencyTeam emergencyTeam : list) {
                        String emergencyTeamName = emergencyTeam.getEmergencyTeamname();
                        teamName.add(emergencyTeamName);
                    }
                }

            }

        }
        // 查询对应的应急预案启动记录处置程序
        List<EmergencyPlanDisposalProcedure> procedureList = emergencyPlanDisposalProcedureService.lambdaQuery()
                .eq(EmergencyPlanDisposalProcedure::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId, id).list();
        this.disposalProcedureTranslate(procedureList);

        //应急预案附件
        List<EmergencyPlanAtt> recordAttList = emergencyPlanAttService.lambdaQuery()
                .eq(EmergencyPlanAtt::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanAtt::getEmergencyPlanId, id).list();

        planDto.setEmergencyTeamId(teamName);
        planDto.setEmergencyPlanDisposalProcedure(procedureList);
        planDto.setEmergencyPlanAtt(recordAttList);


        return planDto;
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

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        EmergencyPlan emergencyPlan = this.getById(businessKey);
        if (ObjectUtil.isEmpty(emergencyPlan)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
        switch (states) {
            case 2:
                // 分部主任审核
                emergencyPlan.setStatus(EmergencyPlanConstant.UNDER_REVIEW);
                break;
            case 3:
                // 分部主任驳回，更新状态为待提交状态
                emergencyPlan.setStatus(EmergencyPlanConstant.REJECTED);
                break;
            case 4:
                // 安技部审核
                emergencyPlan.setStatus(EmergencyPlanConstant.UNDER_REVIEW);
                break;
            case 5:
                // 安技部驳回
                emergencyPlan.setStatus(EmergencyPlanConstant.REJECTED);
                break;
            case 6:
                // 已通过
                emergencyPlan.setStatus(EmergencyPlanConstant.PASSED);
                if(ObjectUtil.isNotEmpty(emergencyPlan.getOldPlanId())){
                    List<EmergencyPlan> list = emergencyPlanService.lambdaQuery()
                            .eq(EmergencyPlan::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                            .eq(EmergencyPlan::getId, emergencyPlan.getOldPlanId()).list();
                    list.stream().forEach(l->{
                        l.setStatus(EmergencyPlanConstant.STOPPED);
                    });
                }
                break;
        }
        this.updateById(emergencyPlan);
    }
}
