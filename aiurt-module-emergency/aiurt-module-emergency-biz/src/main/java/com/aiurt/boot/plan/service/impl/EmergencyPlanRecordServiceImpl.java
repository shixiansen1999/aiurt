package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDepartDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordQueryDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.EmergencyPlanRecordMapper;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.rehearsal.dto.EmergencyDeptDTO;
import com.aiurt.boot.rehearsal.entity.*;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordMonthVO;
import com.aiurt.boot.rehearsal.vo.EmergencyRecordReadOneVO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private EmergencyPlanRecordMapper emergencyPlanRecordMapper;
    @Autowired
    private IEmergencyPlanService emergencyPlanService;
    @Autowired
    private IEmergencyTeamService emergencyTeamService;

    @Override
    public IPage<EmergencyPlanRecordDTO> queryPageList(Page<EmergencyPlanRecordDTO> page, EmergencyPlanRecordQueryDTO emergencyPlanRecordQueryDto) {
       // 根据当前登录人的部门权限和记录的组织部门过滤数据
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        List<CsUserDepartModel> deptModel = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> orgCodes = deptModel.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode()))
                .map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        IPage<EmergencyPlanRecordDTO> pageList = emergencyPlanRecordMapper.queryPageList(page, emergencyPlanRecordQueryDto,orgCodes);
        //启动应急预案名称
        if(CollUtil.isNotEmpty(pageList.getRecords())){
            for (EmergencyPlanRecordDTO record : pageList.getRecords()) {
                String emergencyPlanId = record.getEmergencyPlanId();
                String emergencyPlanVersion = record.getEmergencyPlanVersion();
                List<EmergencyPlan> list = emergencyPlanService.lambdaQuery().eq(EmergencyPlan::getId, emergencyPlanId).list();
                for (EmergencyPlan emergencyPlan : list) {
                    String emergencyPlanName = emergencyPlan.getEmergencyPlanName();
                    record.setPlanVersion(emergencyPlanName+"v"+emergencyPlanVersion);
                }
            }
        }
        Map<String, String> orgMap = sysBaseApi.getAllSysDepart().stream()
                .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
        pageList.getRecords().stream().forEach(l -> {
            List<EmergencyPlanRecordDepart> recordDeparts = emergencyPlanRecordDepartService.lambdaQuery()
                    .eq(EmergencyPlanRecordDepart::getId, l.getId()).list();
            if (CollectionUtil.isNotEmpty(recordDeparts)) {
                List<EmergencyPlanRecordDepartDTO> depts = new ArrayList<>();
                recordDeparts.forEach(d -> depts.add(new EmergencyPlanRecordDepartDTO(d.getOrgCode(), orgMap.get(d.getOrgCode()))));
                l.setEmergencyPlanRecordDepartId(depts);
            }
        });
        return pageList;
    }

    @Override
    public String saveAndAdd(EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        EmergencyPlanRecord emergencyPlanRecord = new EmergencyPlanRecord();
        BeanUtils.copyProperties(emergencyPlanRecordDto, emergencyPlanRecord);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Double version =1.0;
        emergencyPlanRecord.setEmergencyPlanVersion(String.valueOf(version));
        String username = loginUser.getUsername();
        emergencyPlanRecord.setRecorderId(username);
        emergencyPlanRecord.setRecordTime(new Date());
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
        List<EmergencyPlanRecordDepartDTO> emergencyPlanRecordDepartId = emergencyPlanRecordDto.getEmergencyPlanRecordDepartId();
        if(CollUtil.isNotEmpty(emergencyPlanRecordDepartId)){
            for (EmergencyPlanRecordDepartDTO s : emergencyPlanRecordDepartId) {
                EmergencyPlanRecordDepart emergencyPlanRecordDepart = new EmergencyPlanRecordDepart();
                emergencyPlanRecordDepart.setOrgCode(s.getOrgCode());
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
        List<EmergencyPlanRecordDepartDTO> emergencyPlanRecordDepartId = emergencyPlanRecordDto.getEmergencyPlanRecordDepartId();
        if(CollUtil.isNotEmpty(emergencyPlanRecordDepartId)){
            for (EmergencyPlanRecordDepartDTO s : emergencyPlanRecordDepartId) {
                EmergencyPlanRecordDepart emergencyPlanRecordDepart = new EmergencyPlanRecordDepart();
                emergencyPlanRecordDepart.setOrgCode(s.getOrgCode());
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

    @Override
    public EmergencyPlanRecordDTO queryById(String id) {
        EmergencyPlanRecord planRecord = this.getById(id);
        Assert.notNull(planRecord, "未找到对应记录！");
        EmergencyPlanRecordDTO recordDto = new EmergencyPlanRecordDTO();
        BeanUtils.copyProperties(planRecord, recordDto);

        // 获取应急队伍
        List<EmergencyPlanRecordTeam> teamList = emergencyPlanRecordTeamService.lambdaQuery()
                .eq(EmergencyPlanRecordTeam::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordTeam::getEmergencyPlanRecordId, id).list();
        List<String> teamName = new ArrayList<>();
        if(CollUtil.isNotEmpty(teamList)){
            for (EmergencyPlanRecordTeam planRecordTeam : teamList) {
                List<EmergencyTeam> list = emergencyTeamService.lambdaQuery().eq(EmergencyTeam::getId, planRecordTeam.getEmergencyTeamId()).list();
                if(CollUtil.isNotEmpty(list)){
                    for (EmergencyTeam emergencyTeam : list) {
                        String emergencyTeamName = emergencyTeam.getEmergencyTeamname();
                        teamName.add(emergencyTeamName);
                    }
                }

            }

        }
        // 查询对应的参与部门
        List<EmergencyPlanRecordDepart> deptList = emergencyPlanRecordDepartService.lambdaQuery()
                .eq(EmergencyPlanRecordDepart::getEmergencyPlanLaunchRecordId, id).list();
        List<EmergencyPlanRecordDepartDTO> depts = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(deptList)) {
            Map<String, String> orgMap = sysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            deptList.forEach(l -> depts.add(new EmergencyPlanRecordDepartDTO(l.getOrgCode(), orgMap.get(l.getOrgCode()))));
            String deptNames = depts.stream().map(EmergencyPlanRecordDepartDTO::getOrgName).collect(Collectors.joining(";"));
            recordDto.setDeptNames(deptNames);
        }

        // 查询对应的应急预案启动记录处置程序
        List<EmergencyPlanRecordDisposalProcedure> procedureList = emergencyPlanRecordDisposalProcedureService.lambdaQuery()
                .eq(EmergencyPlanRecordDisposalProcedure::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordDisposalProcedure::getEmergencyPlanRecordId, id).list();

        // 查询对应的应急物资
//        List<EmergencyPlanRecordMaterials> materialsList = emergencyPlanRecordMaterialsService.lambdaQuery()
//                .eq(EmergencyPlanRecordMaterials::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
//                .eq(EmergencyPlanRecordMaterials::getEmergencyPlanRecordId, id).list();

        //应急预案附件
        List<EmergencyPlanRecordAtt> recordAttList = emergencyPlanRecordAttService.lambdaQuery()
                .eq(EmergencyPlanRecordAtt::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordAtt::getEmergencyPlanRecordId, id).list();
        //应急预案启动记录事件问题措施添加
        List<EmergencyPlanRecordProblemMeasures> problemMeasuresList = emergencyPlanRecordProblemMeasuresService.lambdaQuery()
                .eq(EmergencyPlanRecordProblemMeasures::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordProblemMeasures::getEmergencyPlanRecordId, id).list();

        recordDto.setEmergencyPlanRecordTeamId(teamName);
        recordDto.setEmergencyPlanRecordDepartId(depts);
        recordDto.setEmergencyPlanRecordDisposalProcedureList(procedureList);
//        recordDto.setEmergencyPlanRecordMaterialsList(materialsList);
        recordDto.setEmergencyPlanRecordAttList(recordAttList);
        recordDto.setEmergencyPlanRecordProblemMeasuresList(problemMeasuresList);
        return recordDto;
    }
}
