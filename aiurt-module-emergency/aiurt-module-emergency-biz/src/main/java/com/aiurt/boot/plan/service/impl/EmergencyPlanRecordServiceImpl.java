package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.EmergencyPlanRecordMapper;
import com.aiurt.boot.plan.service.*;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyPlanRecordServiceImpl extends ServiceImpl<EmergencyPlanRecordMapper, EmergencyPlanRecord> implements IEmergencyPlanRecordService {

    @Autowired
    private IEmergencyPlanRecordTeamService emergencyPlanRecordTeamService;
    @Autowired
    private IEmergencyPlanRecordDepartService emergencyPlanRecordDepartService;
    @Autowired
    private IEmergencyPlanRecordDisposalProcedureService emergencyPlanRecordDisposalProcedureService;
    @Autowired
    private IEmergencyPlanRecordMaterialsService emergencyPlanRecordMaterialsService;
    @Autowired
    private IEmergencyPlanRecordAttService emergencyPlanRecordAttService;
    @Autowired
    private IEmergencyPlanRecordProblemMeasuresService emergencyPlanRecordProblemMeasuresService;

    @Override
    public String saveAndAdd(EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        EmergencyPlanRecord emergencyPlanRecord = new EmergencyPlanRecord();
        BeanUtils.copyProperties(emergencyPlanRecordDto, emergencyPlanRecord);
        Double version =1.0;
        emergencyPlanRecord.setEmergencyPlanVersion(String.valueOf(version));
        this.save(emergencyPlanRecord);

        String id = emergencyPlanRecord.getId();
        //应急救援队伍关联
        List<String> emergencyRecordTeamId = emergencyPlanRecordDto.getEmergencyPlanRecordTeamId();
        if(CollUtil.isNotEmpty(emergencyRecordTeamId)){
            for (String s : emergencyRecordTeamId) {
                EmergencyPlanRecordTeam emergencyPlanRecordTeam = new EmergencyPlanRecordTeam();
                emergencyPlanRecordTeam.setEmergencyTeamId(s);
                emergencyPlanRecordTeam.setEmergencyPlanRecordId(id);
                emergencyPlanRecordTeamService.save(emergencyPlanRecordTeam);
            }
        }
        //参与部门
        List<String> emergencyPlanRecordDepartId = emergencyPlanRecordDto.getEmergencyPlanRecordDepartId();
        if(CollUtil.isNotEmpty(emergencyPlanRecordDepartId)){
            for (String s : emergencyPlanRecordDepartId) {
                EmergencyPlanRecordDepart emergencyPlanRecordDepart = new EmergencyPlanRecordDepart();
                emergencyPlanRecordDepart.setOrgCode(s);
                emergencyPlanRecordDepart.setEmergencyPlanLaunchRecordId(id);
                emergencyPlanRecordDepartService.save(emergencyPlanRecordDepart);
            }
        }
        //应急预案处置程序添加
        List<EmergencyPlanRecordDisposalProcedure> emergencyPlanRecordDisposalProcedureList = emergencyPlanRecordDto.getEmergencyPlanRecordDisposalProcedureList();
        if(CollUtil.isNotEmpty(emergencyPlanRecordDisposalProcedureList)){
            for (EmergencyPlanRecordDisposalProcedure emergencyPlanRecordDisposalProcedure : emergencyPlanRecordDisposalProcedureList) {
                emergencyPlanRecordDisposalProcedure.setEmergencyPlanRecordId(id);
                emergencyPlanRecordDisposalProcedureService.save(emergencyPlanRecordDisposalProcedure);
            }
        }
        //应急物资添加
        List<EmergencyPlanRecordMaterials> emergencyPlanRecordMaterialsList = emergencyPlanRecordDto.getEmergencyPlanRecordMaterialsList();
        if(CollUtil.isNotEmpty(emergencyPlanRecordMaterialsList)){
            for (EmergencyPlanRecordMaterials emergencyPlanRecordMaterial : emergencyPlanRecordMaterialsList) {
                emergencyPlanRecordMaterial.setEmergencyPlanRecordId(id);
                emergencyPlanRecordMaterialsService.save(emergencyPlanRecordMaterial);
            }
        }
        //应急预案启动记录事件材料附件添加
        List<EmergencyPlanRecordAtt> emergencyPlanRecordAttList = emergencyPlanRecordDto.getEmergencyPlanRecordAttList();
        if(CollUtil.isNotEmpty(emergencyPlanRecordAttList)){
            for (EmergencyPlanRecordAtt planRecordAtt : emergencyPlanRecordAttList) {
                planRecordAtt.setEmergencyPlanRecordId(id);
                emergencyPlanRecordAttService.save(planRecordAtt);
            }
        }
        //应急预案启动记录事件问题措施添加
        List<EmergencyPlanRecordProblemMeasures> emergencyPlanRecordProblemMeasuresList = emergencyPlanRecordDto.getEmergencyPlanRecordProblemMeasuresList();
        if(CollUtil.isNotEmpty(emergencyPlanRecordProblemMeasuresList)){
            for (EmergencyPlanRecordProblemMeasures planRecordProblemMeasures : emergencyPlanRecordProblemMeasuresList) {
                planRecordProblemMeasures.setEmergencyPlanRecordId(id);
                emergencyPlanRecordProblemMeasuresService.save(planRecordProblemMeasures);
            }
        }
        return id;
    }

    @Override
    public String edit(EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        String id = emergencyPlanRecordDto.getId();

        Assert.notNull(id, "记录ID为空！");
        EmergencyPlanRecord emPlanRecord = this.getById(id);
        Assert.notNull(emPlanRecord, "未找到对应数据！");

        EmergencyPlanRecord emergencyPlanRecord = new EmergencyPlanRecord();
        BeanUtils.copyProperties(emergencyPlanRecordDto, emergencyPlanRecord);
        this.updateById(emergencyPlanRecord);

        //应急队伍关联
        QueryWrapper<EmergencyPlanRecordTeam> planTeamWrapper = new QueryWrapper<>();
        planTeamWrapper.lambda().eq(EmergencyPlanRecordTeam::getEmergencyPlanRecordId, id);
        emergencyPlanRecordTeamService.remove(planTeamWrapper);
        List<String> emergencyRecordTeamId = emergencyPlanRecordDto.getEmergencyPlanRecordTeamId();
        if(CollUtil.isNotEmpty(emergencyRecordTeamId)){
            for (String s : emergencyRecordTeamId) {
                EmergencyPlanRecordTeam emergencyPlanRecordTeam = new EmergencyPlanRecordTeam();
                emergencyPlanRecordTeam.setEmergencyTeamId(s);
                emergencyPlanRecordTeam.setEmergencyPlanRecordId(id);
                emergencyPlanRecordTeamService.save(emergencyPlanRecordTeam);
            }
        }
        //参与部门关联
        QueryWrapper<EmergencyPlanRecordDepart> planRecordDepartWrapper = new QueryWrapper<>();
        planRecordDepartWrapper.lambda().eq(EmergencyPlanRecordDepart::getEmergencyPlanLaunchRecordId, id);
        emergencyPlanRecordDepartService.remove(planRecordDepartWrapper);
        List<String> emergencyPlanRecordDepartId = emergencyPlanRecordDto.getEmergencyPlanRecordDepartId();
        if(CollUtil.isNotEmpty(emergencyPlanRecordDepartId)){
            for (String s : emergencyPlanRecordDepartId) {
                EmergencyPlanRecordDepart emergencyPlanRecordDepart = new EmergencyPlanRecordDepart();
                emergencyPlanRecordDepart.setOrgCode(s);
                emergencyPlanRecordDepart.setEmergencyPlanLaunchRecordId(id);
                emergencyPlanRecordDepartService.save(emergencyPlanRecordDepart);
            }
        }
        //应急预案启动记录处置程序编辑
        QueryWrapper<EmergencyPlanRecordDisposalProcedure> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyPlanRecordDisposalProcedure::getEmergencyPlanRecordId, id);
        emergencyPlanRecordDisposalProcedureService.remove(wrapper);
        List<EmergencyPlanRecordDisposalProcedure> emergencyPlanRecordDisposalProcedureList = emergencyPlanRecordDto.getEmergencyPlanRecordDisposalProcedureList();
        if (CollectionUtil.isNotEmpty(emergencyPlanRecordDisposalProcedureList)) {
            emergencyPlanRecordDisposalProcedureList.forEach(l -> {
                l.setEmergencyPlanRecordId(emergencyPlanRecord.getId());
            });
            emergencyPlanRecordDisposalProcedureService.saveBatch(emergencyPlanRecordDisposalProcedureList);
        }
        //应急物资编辑
        QueryWrapper<EmergencyPlanRecordMaterials> planRecordMaterialsWrapper = new QueryWrapper<>();
        planRecordMaterialsWrapper.lambda().eq(EmergencyPlanRecordMaterials::getEmergencyPlanRecordId, id);
        emergencyPlanRecordMaterialsService.remove(planRecordMaterialsWrapper);
        List<EmergencyPlanRecordMaterials> emergencyPlanRecordMaterialsList = emergencyPlanRecordDto.getEmergencyPlanRecordMaterialsList();
        if (CollectionUtil.isNotEmpty(emergencyPlanRecordMaterialsList)) {
            emergencyPlanRecordMaterialsList.forEach(l -> {
                l.setEmergencyPlanRecordId(emergencyPlanRecord.getId());
            });
            emergencyPlanRecordMaterialsService.saveBatch(emergencyPlanRecordMaterialsList);
        }
        //应急预案附件
        QueryWrapper<EmergencyPlanRecordAtt> planRecordAttWrapper = new QueryWrapper<>();
        planRecordAttWrapper.lambda().eq(EmergencyPlanRecordAtt::getEmergencyPlanRecordId,id);
        emergencyPlanRecordAttService.remove(planRecordAttWrapper);
        List<EmergencyPlanRecordAtt> emergencyPlanRecordAttList = emergencyPlanRecordDto.getEmergencyPlanRecordAttList();
        if (CollectionUtil.isNotEmpty(emergencyPlanRecordAttList)) {
            emergencyPlanRecordAttList.forEach(l -> {
                l.setEmergencyPlanRecordId(emergencyPlanRecord.getId());
            });
            emergencyPlanRecordAttService.saveBatch(emergencyPlanRecordAttList);
        }
        //应急预案启动记录事件问题措施添加
        QueryWrapper<EmergencyPlanRecordProblemMeasures> planRecordProblemMeasuresWrapper = new QueryWrapper<>();
        planRecordProblemMeasuresWrapper.lambda().eq(EmergencyPlanRecordProblemMeasures::getEmergencyPlanRecordId,id);
        emergencyPlanRecordProblemMeasuresService.remove(planRecordProblemMeasuresWrapper);
        List<EmergencyPlanRecordProblemMeasures> emergencyPlanRecordProblemMeasuresList = emergencyPlanRecordDto.getEmergencyPlanRecordProblemMeasuresList();
        if (CollectionUtil.isNotEmpty(emergencyPlanRecordProblemMeasuresList)) {
            emergencyPlanRecordProblemMeasuresList.forEach(l -> {
                l.setEmergencyPlanRecordId(emergencyPlanRecord.getId());
            });
            emergencyPlanRecordProblemMeasuresService.saveBatch(emergencyPlanRecordProblemMeasuresList);
        }
        return id;
    }

    @Override
    public void delete(String id) {
        EmergencyPlanRecord emPlanRecord = this.getById(id);
        Assert.notNull(emPlanRecord, "未找到对应数据！");
        this.removeById(id);

        //关联应急队伍删除
        QueryWrapper<EmergencyPlanRecordTeam> planRecordTeamWrapper = new QueryWrapper<>();
        planRecordTeamWrapper.lambda().eq(EmergencyPlanRecordTeam::getEmergencyPlanRecordId, id);
        emergencyPlanRecordTeamService.remove(planRecordTeamWrapper);
        //参与部门关联
        QueryWrapper<EmergencyPlanRecordDepart> planRecordDepartWrapper = new QueryWrapper<>();
        planRecordDepartWrapper.lambda().eq(EmergencyPlanRecordDepart::getEmergencyPlanLaunchRecordId, id);
        emergencyPlanRecordDepartService.remove(planRecordDepartWrapper);
        //应急预案启动记录处置程序编辑
        QueryWrapper<EmergencyPlanRecordDisposalProcedure> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(EmergencyPlanRecordDisposalProcedure::getEmergencyPlanRecordId, id);
        emergencyPlanRecordDisposalProcedureService.remove(wrapper);
        //应急物资编辑
        QueryWrapper<EmergencyPlanRecordMaterials> planRecordMaterialsWrapper = new QueryWrapper<>();
        planRecordMaterialsWrapper.lambda().eq(EmergencyPlanRecordMaterials::getEmergencyPlanRecordId, id);
        emergencyPlanRecordMaterialsService.remove(planRecordMaterialsWrapper);
        //应急预案附件
        QueryWrapper<EmergencyPlanRecordAtt> planRecordAttWrapper = new QueryWrapper<>();
        planRecordAttWrapper.lambda().eq(EmergencyPlanRecordAtt::getEmergencyPlanRecordId,id);
        emergencyPlanRecordAttService.remove(planRecordAttWrapper);
        //应急预案启动记录事件问题措施添加
        QueryWrapper<EmergencyPlanRecordProblemMeasures> planRecordProblemMeasuresWrapper = new QueryWrapper<>();
        planRecordProblemMeasuresWrapper.lambda().eq(EmergencyPlanRecordProblemMeasures::getEmergencyPlanRecordId,id);
        emergencyPlanRecordProblemMeasuresService.remove(planRecordProblemMeasuresWrapper);
    }
}
