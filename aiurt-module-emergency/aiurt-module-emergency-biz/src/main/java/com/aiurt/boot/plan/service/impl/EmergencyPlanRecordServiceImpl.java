package com.aiurt.boot.plan.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.aiurt.boot.materials.service.IEmergencyMaterialsUsageService;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.controller.PlanRecordExcelListener;
import com.aiurt.boot.plan.controller.RecordExcelListener;
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
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.XlsUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDeptUserModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;

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
                List<EmergencyPlan> list = emergencyPlanService.lambdaQuery().eq(EmergencyPlan::getId, emergencyPlanId).list();
                for (EmergencyPlan emergencyPlan : list) {
                    String emergencyPlanName = emergencyPlan.getEmergencyPlanName();
                    String emergencyPlanVersion = emergencyPlan.getEmergencyPlanVersion();
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
            String emergencyPlanVersion =p.getEmergencyPlanVersion();
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
    public String submit(EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        String id = emergencyPlanRecordDto.getId();
        if(ObjectUtil.isEmpty(id)){
            emergencyPlanRecordDto.setStatus(EmergencyPlanConstant.IS_SUBMIT1);
            String s = saveAndAdd(emergencyPlanRecordDto);
            addMaterials(s);
        }else{
            EmergencyPlanRecord emergencyPlanRecord = this.getById(id);
            Assert.notNull(emergencyPlanRecord, "未找到对应数据！");
            //状态改为已提交
            if(EmergencyPlanConstant.IS_SUBMIT1.equals(emergencyPlanRecordDto.getStatus())){
                throw new AiurtBootException("该启动预案已经提交，无需重复提交！");
            }else{
                emergencyPlanRecord.setStatus(EmergencyPlanConstant.IS_SUBMIT1);
                this.updateById(emergencyPlanRecord);
            }

        }
        return id;
    }

    /**
     * 物资使用记录添加一条数据
     * @param id
     */
    public void addMaterials(String id){
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

    @Override
    public void exportTemplateXls(HttpServletResponse response,HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/emergencyPlanRecord.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyPlanRecord.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String path = fileTemp.getAbsolutePath();
        TemplateExportParams exportParams = new TemplateExportParams(path);
        Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>(16);
        Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);
        CommonAPI bean = SpringContextUtils.getBean(CommonAPI.class);
        List<DictModel> isTypeModels = bean.queryDictItemsByCode("emergency_event_class");
        EmergencyPlanServiceImpl.ExcelSelectListUtil.selectList(workbook, "事件类型", 0, 3, isTypeModels);
        List<DictModel> isProperty = bean.queryDictItemsByCode("emergency_event_property");
        EmergencyPlanServiceImpl.ExcelSelectListUtil.selectList(workbook, "事件性质", 3, 6, isProperty);
        String fileName = "应急预案导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "应急预案启动记录导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, EmergencyPlanRecordDTO emergencyPlanRecordDto) {
        // 封装数据
        List<EmergencyPlanRecordExcelDTO> pageList = this.getinspectionStrategyList(emergencyPlanRecordDto);

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "应急预案数据";
        ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), ExcelType.XSSF);
        //调用ExcelExportUtil.exportExcel方法生成workbook
        Workbook wb = ExcelExportUtil.exportExcel(exportParams, EmergencyPlanExcelDTO.class, pageList);
        String fileName = "应急预案数据";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            //xlsx格式设置
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            wb.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取excel表格数据
     *
     * @param emergencyPlanRecordDto
     * @return
     */
    private List<EmergencyPlanRecordExcelDTO> getinspectionStrategyList(EmergencyPlanRecordDTO emergencyPlanRecordDto) {

        List<EmergencyPlanRecordExcelDTO> result =emergencyPlanRecordMapper.selectListNoPage(emergencyPlanRecordDto);
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        //成功条数
        Integer successlines = 0;
        // 失败条数
        Integer  errorLines = 0;
        // 标记是否有错误信息
        Boolean errorSign = false;
        // 标记是否有错误信息
        Boolean errorSign2 = false;
        // 失败导出的excel下载地址
        String failReportUrl = "";

        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();

            // 判断是否xls、xlsx两种类型的文件，不是则直接返回
            String type = FilenameUtils.getExtension(file.getOriginalFilename());
            if (!StrUtil.equalsAny(type, true, "xls", "xlsx")) {
                return imporReturnRes(errorLines, false, failReportUrl,"文件导入失败，文件类型不对");
            }
            //读取数据监听
            PlanRecordExcelListener recordExcelListener =new PlanRecordExcelListener();
            //读取数据
            try {
                EasyExcel.read(file.getInputStream(), RecordData.class, recordExcelListener).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //判断读取的数据是否有空行
            EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO = recordExcelListener.getEmergencyPlanRecordImportExcelDTO();
            List<EmergencyPlanRecordDisposalProcedureImportExcelDTO> planRecordDisposalProcedureList = emergencyPlanRecordImportExcelDTO.getPlanRecordDisposalProcedureList();
            List<EmergencyPlanRecordMaterialsImportExcelDTO> planRecordMaterialsList = emergencyPlanRecordImportExcelDTO.getPlanRecordMaterialsList();
            List<EmergencyPlanRecordProblemMeasuresImportExcelDTO> problemMeasuresList = emergencyPlanRecordImportExcelDTO.getProblemMeasuresList();
            if(CollUtil.isNotEmpty(planRecordDisposalProcedureList)){
                Iterator<EmergencyPlanRecordDisposalProcedureImportExcelDTO> iterator = planRecordDisposalProcedureList.iterator();
                if(CollUtil.isNotEmpty(iterator)){
                    while (iterator.hasNext()) {
                        EmergencyPlanRecordDisposalProcedureImportExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
                if (CollUtil.isEmpty(planRecordDisposalProcedureList)) {
                    return Result.error("文件导入失败:应急预案处置程序不能为空！");
                }
            }
            if(CollUtil.isNotEmpty(planRecordMaterialsList)){
                Iterator<EmergencyPlanRecordMaterialsImportExcelDTO> iterator = planRecordMaterialsList.iterator();
                if(CollUtil.isNotEmpty(iterator)){
                    while (iterator.hasNext()) {
                        EmergencyPlanRecordMaterialsImportExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
            }
            if(CollUtil.isNotEmpty(problemMeasuresList)){
                Iterator<EmergencyPlanRecordProblemMeasuresImportExcelDTO> iterator = problemMeasuresList.iterator();
                if(CollUtil.isNotEmpty(iterator)){
                    while (iterator.hasNext()) {
                        EmergencyPlanRecordProblemMeasuresImportExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
            }

            // 记录校验得到的错误信息
            StringBuilder errorMessage = new StringBuilder();

            // 校验数据
            EmergencyPlanRecordDTO emergencyPlanRecordDTO = new EmergencyPlanRecordDTO();
            // 校验应急启动记录
            this.checkData(errorMessage, emergencyPlanRecordImportExcelDTO, emergencyPlanRecordDTO);
            // 校验应急预案处置程序
            errorSign=this.checkDisposalProcedureCode(errorSign, emergencyPlanRecordImportExcelDTO, emergencyPlanRecordDTO);
            // 校验应急预案物资清单
            errorSign2=this.checkMaterialsCode(errorSign2, emergencyPlanRecordImportExcelDTO, emergencyPlanRecordDTO);

            if (errorMessage.length() > 0 || errorSign || errorSign2) {
                if(errorMessage.length() > 0  ){
                    errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                    emergencyPlanRecordImportExcelDTO.setEmergencyPlanRecordErrorReason(errorMessage.toString());
                }
                errorLines++;
            }

            // 存在错误，错误报告下载
            if (errorLines > 0) {
                try {
                    return getErrorExcel(errorLines, emergencyPlanRecordImportExcelDTO, failReportUrl, type);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                // 校验通过，保存到系统
                if (ObjectUtil.isNotEmpty(emergencyPlanRecordDTO)) {
//                    this.saveAndAdd(emergencyPlanRecordDTO);
                    return imporReturnRes(errorLines, true, failReportUrl,"文件导入成功");
                }
            }
        }
        return imporReturnRes(errorLines, false, failReportUrl,"暂无导入数据");
    }

    /**
     * 检修策略导入统一返回格式
     * @param errorLines 错误条数
     * @param isSucceed 是否成功
     * @param failReportUrl 错误报告下载地址
     * @param message 提示信息
     * @return
     */
    public static Result<?> imporReturnRes(int errorLines, boolean isSucceed, String failReportUrl,String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("failReportUrl", failReportUrl);
        Result res = Result.ok(result);
        res.setMessage(message);
        res.setCode(200);
        return res;
    }

    /**
     * 校验excel数据
     *
     * @param errorMessage                错误信息
     * @param emergencyPlanRecordImportExcelDTO excel数据
     * @param emergencyPlanRecordDTO       转换成要保存的实体数据
     */
    private void checkData(StringBuilder errorMessage, EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO, EmergencyPlanRecordDTO emergencyPlanRecordDTO) {
        // 空数据不处理
        if (ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO)) {
            return;
        }
        // 应急预案必填校验
        requiredCheck(errorMessage, emergencyPlanRecordImportExcelDTO, emergencyPlanRecordDTO);
    }
    private void requiredCheck(StringBuilder errorMessage, EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO, EmergencyPlanRecordDTO emergencyPlanRecordDTO) {
        //获取部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        //编制部门
        emergencyPlanRecordDTO.setOrgCode(orgCode);
        //预案版本默认值
        emergencyPlanRecordDTO.setEmergencyPlanVersion("1.0");
        // 应急预案类型转换
        HashMap<String, Integer> checkMap = CollUtil.newHashMap();
        checkMap.put("事件分类1", 1);
        checkMap.put("事件分类2", 2);
        HashMap<String, Integer> checkMap2 = CollUtil.newHashMap();
        checkMap2.put("运营事件", 1);
        checkMap2.put("非运营事件", 2);
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEventClass())){
            errorMessage.append("事件类型必须填写，");
        }else {
            Integer isConfirm = checkMap.get(emergencyPlanRecordImportExcelDTO.getEventClass());
            if (ObjectUtil.isNotEmpty(isConfirm)) {
                emergencyPlanRecordDTO.setEventClass(isConfirm);
            } else {
                errorMessage.append("事件类型类型格式错误，");
            }
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEventProperty())){
            errorMessage.append("事件性质必须填写，");
        }else{
            Integer isConfirm = checkMap2.get(emergencyPlanRecordImportExcelDTO.getEventProperty());
            if (ObjectUtil.isNotEmpty(isConfirm)) {
                emergencyPlanRecordDTO.setEventProperty(isConfirm);
            } else {
                errorMessage.append("事件性质格式错误，");
            }
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEmergencyPlanId())){
            errorMessage.append("应急预案名称必须填写，");
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEmergencyPlanVersion())){
            errorMessage.append("应急预案版本必须填写，");
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getStarttime())){
            errorMessage.append("启动日期必须填写，");
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEmergencyPlanRecordDepartId())){
            errorMessage.append("参与部门必须填写，");
        }
        if(ObjectUtil.isEmpty(emergencyPlanRecordImportExcelDTO.getEmergencyTeamId())){
            errorMessage.append("应急队伍名称必须填写，");
        }else{
            String emergencyTeamId = emergencyPlanRecordImportExcelDTO.getEmergencyTeamId();
            String[] split = emergencyTeamId.split(";");
            List<String> teamList = new ArrayList<>();
            for (String s : split) {
                List<EmergencyTeam> list = emergencyTeamService.lambdaQuery().eq(EmergencyTeam::getEmergencyTeamname, s).list();
                for (EmergencyTeam emergencyTeam : list) {
                    String id = emergencyTeam.getId();
                    teamList.add(id);
                }

            }
            if (ObjectUtil.isNotEmpty(teamList)) {
                emergencyPlanRecordDTO.setEmergencyPlanRecordTeamId(teamList);
            } else {
                errorMessage.append("不存在这个队伍!");
            }

        }
        emergencyPlanRecordDTO.setAdvice(emergencyPlanRecordImportExcelDTO.getAdvice());

    }

    private Boolean checkDisposalProcedureCode(Boolean errorSign, EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO, EmergencyPlanRecordDTO emergencyPlanRecordDTO) {
        return errorSign;
    }
    private Boolean checkMaterialsCode(Boolean errorSign, EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO, EmergencyPlanRecordDTO emergencyPlanRecordDTO) {
        return errorSign;
    }

    /**
     * 下载错误清单
     * @param errorLines
     * @param url
     * @param type
     * @return
     * @throws IOException
     */
    private Result<?> getErrorExcel(int errorLines, EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        Resource resource = new ClassPathResource("/templates/emergencyPlanError.xlsx");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyPlanError.xlsx");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);

            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);

            // 封装数据
            Map<String, Object> errorMap = handleData(emergencyPlanRecordImportExcelDTO);

            // 将数据填入表格
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            String fileName = "应急预案数据导入错误清单" + "_" + System.currentTimeMillis() + "." + type;
            FileOutputStream out = new FileOutputStream(upLoadPath + File.separator + fileName);
            url = fileName;
            workbook.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imporReturnRes(errorLines, false, url,"文件导入失败，数据有错误");
    }

    @NotNull
    private Map<String, Object> handleData(EmergencyPlanRecordImportExcelDTO emergencyPlanRecordImportExcelDTO) {
        Map<String, Object> errorMap = CollUtil.newHashMap();
        List<Map<String, String>> mapList = CollUtil.newArrayList();
        List<Map<String, String>> mapList2 = CollUtil.newArrayList();

        return errorMap;
    }




}
