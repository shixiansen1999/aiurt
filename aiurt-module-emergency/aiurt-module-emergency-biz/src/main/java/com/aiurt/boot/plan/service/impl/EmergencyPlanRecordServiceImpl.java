package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.EmergencyPlanRecordMapper;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.plan.vo.EmergencyPlanRecordVO;
import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
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
import org.jeecg.common.system.vo.SysDeptUserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private IEmergencyMaterialsUsageService iEmergencyMaterialsUsageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<EmergencyPlanRecordVO> queryPageList(Page<EmergencyPlanRecordVO> page, EmergencyPlanRecordQueryDTO emergencyPlanRecordQueryDto) {
       // 根据当前登录人的部门权限和记录的组织部门过滤数据
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Assert.notNull(loginUser, "检测到未登录，请登录后操作！");
        List<CsUserDepartModel> deptModel = sysBaseApi.getDepartByUserId(loginUser.getId());
        List<String> orgCodes = deptModel.stream().filter(l -> StrUtil.isNotEmpty(l.getOrgCode()))
                .map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgCodes)) {
            return page;
        }
        IPage<EmergencyPlanRecordVO> pageList = emergencyPlanRecordMapper.queryPageList(page, emergencyPlanRecordQueryDto,orgCodes);
        //启动应急预案名称
        if(CollUtil.isNotEmpty(pageList.getRecords())){
            for (EmergencyPlanRecordVO record : pageList.getRecords()) {
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
            //应急预案启动记录处置程序
            List<EmergencyPlanRecordDisposalProcedure> procedureList = emergencyPlanRecordDisposalProcedureService.lambdaQuery().eq(EmergencyPlanRecordDisposalProcedure::getEmergencyPlanRecordId, l.getId()).list();
            l.setEmergencyPlanRecordDisposalProcedureList(procedureList);

            //应急队伍
            List<EmergencyPlanRecordTeam> teamList = emergencyPlanRecordTeamService.lambdaQuery().eq(EmergencyPlanRecordTeam::getEmergencyPlanRecordId, l.getId()).list();
            List<EmergencyPlanTeamDTO> teams = new ArrayList<>();
            if(CollUtil.isNotEmpty(teamList)){
                EmergencyPlanTeamDTO emergencyPlanTeamDTO = new EmergencyPlanTeamDTO();
                teamList.forEach(t->{
                    String emergencyTeamId = t.getEmergencyTeamId();
                    String emergencyTeamName = null;
                    List<EmergencyTeam> list = emergencyTeamService.lambdaQuery().eq(EmergencyTeam::getId, emergencyTeamId).list();
                    if(CollUtil.isNotEmpty(list)){
                        for (EmergencyTeam emergencyTeam : list) {
                            emergencyTeamName = emergencyTeam.getEmergencyTeamname();
                        }
                    }
                    emergencyPlanTeamDTO.setEmergencyTeamId(emergencyTeamId);
                    emergencyPlanTeamDTO.setEmergencyTeamName(emergencyTeamName);
                    teams.add(emergencyPlanTeamDTO);
                });
            }
            l.setEmergencyPlanRecordTeamId(teams);

            List<EmergencyPlanRecordDepart> recordDeparts = emergencyPlanRecordDepartService.lambdaQuery()
                    .eq(EmergencyPlanRecordDepart::getEmergencyPlanLaunchRecordId, l.getId()).list();
            if (CollectionUtil.isNotEmpty(recordDeparts)) {
                List<EmergencyPlanRecordDepartDTO> depts = new ArrayList<>();
                recordDeparts.forEach(d -> depts.add(new EmergencyPlanRecordDepartDTO(d.getOrgCode(), orgMap.get(d.getOrgCode()))));
                l.setEmergencyPlanRecordDepartId(depts);
            }
        });
        return pageList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveAndAdd(EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        EmergencyPlanRecord emergencyPlanRecord = new EmergencyPlanRecord();
        BeanUtils.copyProperties(emergencyPlanRecordDto, emergencyPlanRecord);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Double version =1.0;
        emergencyPlanRecord.setEmergencyPlanVersion(String.valueOf(version));
        String username = loginUser.getUsername();
        emergencyPlanRecord.setRecorderId(username);
        emergencyPlanRecord.setOrgCode(loginUser.getOrgCode());
        Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd"));
        emergencyPlanRecord.setRecordTime(nowDate);
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
                emergencyPlanRecordDisposalProcedure.setId(null);
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
        //应急预案启动记录事件总结材料添加
        List<EmergencyPlanRecordAtt> emergencyPlanRecordAttList2 = emergencyPlanRecordDto.getEmergencyPlanRecordAttList2();
        if(CollUtil.isNotEmpty(emergencyPlanRecordAttList2)){
            for (EmergencyPlanRecordAtt planRecordAtt : emergencyPlanRecordAttList2) {
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
    @Transactional(rollbackFor = Exception.class)
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
        //应急预案附件编辑
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
        List<EmergencyPlanRecordAtt> emergencyPlanRecordAttList2 = emergencyPlanRecordDto.getEmergencyPlanRecordAttList2();
        if (CollectionUtil.isNotEmpty(emergencyPlanRecordAttList2)) {
            emergencyPlanRecordAttList2.forEach(l -> {
                l.setEmergencyPlanRecordId(emergencyPlanRecord.getId());
            });
            emergencyPlanRecordAttService.saveBatch(emergencyPlanRecordAttList2);
        }
        //应急预案启动记录事件问题措施编辑
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
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public EmergencyPlanRecordDTO queryById(String id) {
        EmergencyPlanRecord planRecord = this.getById(id);
        Assert.notNull(planRecord, "未找到对应记录！");
        EmergencyPlanRecordDTO recordDto = new EmergencyPlanRecordDTO();
        BeanUtils.copyProperties(planRecord, recordDto);

        //启动应急预案
        List<EmergencyPlan> planList = emergencyPlanService.lambdaQuery()
                .eq(EmergencyPlan::getId, recordDto.getEmergencyPlanId())
                .eq(EmergencyPlan::getDelFlag,EmergencyPlanConstant.DEL_FLAG0)
                .list();
        planList.stream().forEach(p->{
            String emergencyPlanName = p.getEmergencyPlanName();
            String emergencyPlanVersion = p.getEmergencyPlanVersion();
            recordDto.setPlanVersion(emergencyPlanName+"v"+emergencyPlanVersion);
        });

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
          this.disposalProcedureTranslate(procedureList);


        //应急预案启动记录事件相关材料
        List<EmergencyPlanRecordAtt> recordAttList = emergencyPlanRecordAttService.lambdaQuery()
                .eq(EmergencyPlanRecordAtt::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordAtt::getEmergencyPlanRecordId, id)
                .eq(EmergencyPlanRecordAtt::getMaterialType,EmergencyPlanConstant.MATERIAL_TYPE0).list();

        //应急预案启动记录事件总结材料
        List<EmergencyPlanRecordAtt> recordAttList2 = emergencyPlanRecordAttService.lambdaQuery()
                .eq(EmergencyPlanRecordAtt::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordAtt::getEmergencyPlanRecordId, id)
                .eq(EmergencyPlanRecordAtt::getMaterialType,EmergencyPlanConstant.MATERIAL_TYPE1).list();

        //应急预案启动记录事件问题措施
        List<EmergencyPlanRecordProblemMeasures> problemMeasuresList = emergencyPlanRecordProblemMeasuresService.lambdaQuery()
                .eq(EmergencyPlanRecordProblemMeasures::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanRecordProblemMeasures::getEmergencyPlanRecordId, id).list();
        this.questionTranslate(problemMeasuresList);

        recordDto.setEmergencyPlanRecordTeamId(teamName);
        recordDto.setEmergencyPlanRecordDisposalProcedureList(procedureList);
        recordDto.setEmergencyPlanRecordAttList(recordAttList);
        recordDto.setEmergencyPlanRecordAttList2(recordAttList2);
        recordDto.setEmergencyPlanRecordProblemMeasuresList(problemMeasuresList);
        return recordDto;
    }

    @Override
    public List<SysDeptUserModel> getDeptUserGanged() {
        return sysBaseApi.getDeptUserGanged();
    }

    @Override
    public List<LoginUser> getDutyUser() {
        // 责任人根据当前的用户部门筛选出当前部门的所有人
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        if (StrUtil.isEmpty(orgCode)) {
            return Collections.emptyList();
        }
        List<LoginUser> users = sysBaseApi.getUserByDeptCode(orgCode);
        return users;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submit(String id) {
        EmergencyPlanRecord emergencyPlanRecord = this.getById(id);
        Assert.notNull(emergencyPlanRecord, "未找到对应数据！");
        //状态改为已提交
        emergencyPlanRecord.setStatus(EmergencyPlanConstant.IS_SUBMIT1);
        this.updateById(emergencyPlanRecord);

        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<EmergencyPlanRecordMaterials> materialsList = emergencyPlanRecordMaterialsService.lambdaQuery().eq(EmergencyPlanRecordMaterials::getEmergencyPlanRecordId, id).list();
        materialsList.forEach(m->{
            List<EmergencyMaterialsUsage> list = iEmergencyMaterialsUsageService.lambdaQuery().eq(EmergencyMaterialsUsage::getMaterialsCode, m.getMaterialsCode()).list();
            String materialsName = null;
            for (EmergencyMaterialsUsage emergencyMaterialsUsage : list) {
                 materialsName = emergencyMaterialsUsage.getMaterialsName();
            }
            EmergencyMaterialsUsage emergencyMaterialsUsage = new EmergencyMaterialsUsage();
            emergencyMaterialsUsage.setMaterialsCode(m.getMaterialsCode());
            emergencyMaterialsUsage.setMaterialsName(materialsName);
            emergencyMaterialsUsage.setNumber(m.getMaterialsNumber());
            Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            emergencyMaterialsUsage.setUseDate(nowDate);
            emergencyMaterialsUsage.setUseTime(nowDate);
            emergencyMaterialsUsage.setUserId(loginUser.getId());
            iEmergencyMaterialsUsageService.save(emergencyMaterialsUsage);
        });

        return id;
    }

    /**
     * 处置程序字段翻译
     *
     * @param procedureList
     */
    private void disposalProcedureTranslate(List<EmergencyPlanRecordDisposalProcedure> procedureList) {
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

    /**
     * 关联的问题列表的字典，组织机构名称转换
     *
     * @param problemMeasuresList
     */
    private void questionTranslate(List<EmergencyPlanRecordProblemMeasures> problemMeasuresList) {
        if (CollectionUtil.isNotEmpty(problemMeasuresList)) {
            Map<String, String> orgMap = sysBaseApi.getAllSysDepart().stream()
                    .collect(Collectors.toMap(k -> k.getOrgCode(), v -> v.getDepartName(), (a, b) -> a));
            problemMeasuresList.forEach(l -> {
                //责任部门翻译
                l.setOrgName(orgMap.get(l.getOrgCode()));
                //责任部门负责人翻译
                Optional.ofNullable(l.getOrgUserId()).ifPresent(userId -> {
                    LoginUser loginUser = sysBaseApi.getUserById(userId);
                    Optional.ofNullable(loginUser).ifPresent(user -> l.setOrgUserName(user.getRealname()));
                });
                //责任人翻译
                Optional.ofNullable(l.getManagerId()).ifPresent(userId -> {
                    LoginUser loginUser = sysBaseApi.getUserById(userId);
                    Optional.ofNullable(loginUser).ifPresent(user -> l.setUserName(user.getRealname()));
                });
            });
        }
    }
}
