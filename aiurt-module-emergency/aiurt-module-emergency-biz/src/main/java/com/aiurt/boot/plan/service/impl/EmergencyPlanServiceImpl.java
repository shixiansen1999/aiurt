package com.aiurt.boot.plan.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.controller.RecordExcelListener;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.EmergencyPlanMapper;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
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

    @Autowired
    private FlowBaseApi flowBaseApi;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;


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
        IPage<EmergencyPlan> pageList = emergencyPlanMapper.queryPageList(page, emergencyPlanQueryDto, orgCodes);
        List<EmergencyPlan> records = pageList.getRecords();
        if(CollUtil.isNotEmpty(records)){
            for (EmergencyPlan record : records) {
                TaskInfoDTO taskInfoDTO = flowBaseApi.viewRuntimeTaskInfo(record.getProcessInstanceId(), record.getTaskId());
                List<ActOperationEntity> operationList = taskInfoDTO.getOperationList();
                //operationList为空，没有审核按钮
                if(CollUtil.isNotEmpty(operationList)){
                    record.setHaveButton(true);
                }else{
                    record.setHaveButton(false);
                }
            }
        }
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
                    planDisposalProcedure.setId(null);
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
    public EmergencyPlanDTO change(EmergencyPlanDTO emergencyPlanDto) {
        String id = emergencyPlanDto.getId();

        Assert.notNull(id, "记录ID为空！");
        EmergencyPlan emPlan = this.getById(id);
        Assert.notNull(emPlan, "未找到对应数据！");
        // 审核通过才允许变更
        if (!EmergencyPlanConstant.PASSED.equals(emPlan.getEmergencyPlanStatus())) {
            throw new AiurtBootException("未审核通过的预案不能变更！");
        }
//        List<EmergencyPlan> oldPlanList = emergencyPlanService.lambdaQuery().eq(EmergencyPlan::getOldPlanId, id).list();
//        if(oldPlanList.size()>1){
//            throw new AiurtBootException("该预案已经变更过！");
//        }
        //获取部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        //创建新的应急预案
        EmergencyPlanDTO newEmergencyPlanDto = new EmergencyPlanDTO();
        newEmergencyPlanDto.setEmergencyPlanType(emergencyPlanDto.getEmergencyPlanType());
        newEmergencyPlanDto.setEmergencyPlanName(emergencyPlanDto.getEmergencyPlanName());
        newEmergencyPlanDto.setEmergencyPlanContent(emergencyPlanDto.getEmergencyPlanContent());
        newEmergencyPlanDto.setKeyWord(emergencyPlanDto.getKeyWord());
        newEmergencyPlanDto.setEmergencyPlanStatus(EmergencyPlanConstant.TO_SUBMITTED);
        newEmergencyPlanDto.setOrgCode(orgCode);
        String emergencyPlanVersion = emergencyPlanDto.getEmergencyPlanVersion();
        newEmergencyPlanDto.setEmergencyPlanVersion(String.valueOf(Double.valueOf(emergencyPlanVersion)+1));
        newEmergencyPlanDto.setOldPlanId(emergencyPlanDto.getId());
        newEmergencyPlanDto.setStatus(null);
        newEmergencyPlanDto.setEmergencyPlanAtt(emergencyPlanDto.getEmergencyPlanAtt());
        newEmergencyPlanDto.setEmergencyTeamId(emergencyPlanDto.getEmergencyTeamId());
        newEmergencyPlanDto.setEmergencyPlanMaterials(emergencyPlanDto.getEmergencyPlanMaterials());
        newEmergencyPlanDto.setEmergencyPlanDisposalProcedure(emergencyPlanDto.getEmergencyPlanDisposalProcedure());

        //引用流程开始接口
        StartBpmnDTO startBpmnDto  = new StartBpmnDTO();
        startBpmnDto.setModelKey("emergency_plan");
        Map<String,Object> map = new HashMap<>(32);
        map.put("emergencyTeamId",newEmergencyPlanDto.getEmergencyTeamId());
        map.put("emergencyPlanDisposalProcedure",newEmergencyPlanDto.getEmergencyPlanDisposalProcedure());
        map.put("emergencyPlanMaterials",newEmergencyPlanDto.getEmergencyPlanMaterials());
        map.put("emergencyPlanAtt",newEmergencyPlanDto.getEmergencyPlanAtt());
        map.put("emergencyPlanType",newEmergencyPlanDto.getEmergencyPlanType());
        map.put("keyWord",newEmergencyPlanDto.getKeyWord());
        map.put("emergencyPlanContent",newEmergencyPlanDto.getEmergencyPlanContent());
        map.put("orgCode",newEmergencyPlanDto.getOrgCode());
        map.put("emergencyPlanVersion",newEmergencyPlanDto.getEmergencyPlanVersion());
        map.put("emergencyPlanName",newEmergencyPlanDto.getEmergencyPlanName());
        map.put("emergencyPlanStatus",newEmergencyPlanDto.getEmergencyPlanStatus());
        map.put("oldPlanId",newEmergencyPlanDto.getOldPlanId());
        startBpmnDto.setBusData(map);
        FlowTaskCompleteCommentDTO flowTaskCompleteCommentDTO = new FlowTaskCompleteCommentDTO();
        flowTaskCompleteCommentDTO.setApprovalType("save");
        startBpmnDto.setFlowTaskCompleteDTO(flowTaskCompleteCommentDTO);
        flowBaseApi.startAndTakeFirst(startBpmnDto);

        return newEmergencyPlanDto;
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
        List<String> teamId = new ArrayList<>();
        if(CollUtil.isNotEmpty(teamList)){
            for (EmergencyPlanTeam planTeam : teamList) {
                teamId.add(planTeam.getEmergencyTeamId());
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

        planDto.setEmergencyTeamId(teamId);
        planDto.setEmergencyTeamName(teamName);
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
                emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.UNDER_REVIEW);
                break;
            case 3:
                // 分部主任驳回，更新状态为待提交状态
                emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.REJECTED);
                break;
            case 4:
                // 安技部审核
                emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.UNDER_REVIEW);
                break;
            case 5:
                // 安技部驳回
                emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.REJECTED);
                break;
            case 6:
                // 已通过
                emergencyPlan.setEmergencyPlanStatus(EmergencyPlanConstant.PASSED);
                //通过后设置为有效
                emergencyPlan.setStatus(EmergencyPlanConstant.VALID);
                //新版本更新后，更改旧版本的状态为停用
                if(ObjectUtil.isNotEmpty(emergencyPlan.getOldPlanId())){
                    List<EmergencyPlan> list = emergencyPlanService.lambdaQuery()
                            .eq(EmergencyPlan::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                            .eq(EmergencyPlan::getId, emergencyPlan.getOldPlanId()).list();
                    for (EmergencyPlan plan : list) {
                        plan.setStatus(EmergencyPlanConstant.STOPPED);
                        this.updateById(plan);
                    }
                }
                Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                emergencyPlan.setApprovedTime(nowDate);
                break;
        }
        this.updateById(emergencyPlan);
    }


    @Override
    public void exportTemplateXls(HttpServletResponse response,HttpServletRequest request) throws IOException {
        //获取输入流，原始模板位置
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/emergencyPlan.xlsx");
        InputStream resourceAsStream = resource.getInputStream();
        //2.获取临时文件
        File fileTemp = new File("/templates/emergencyPlan.xlsx");
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
        List<DictModel> isTypeModels = bean.queryDictItemsByCode("emergency_plan_type");
        ExcelSelectListUtil.selectList(workbook, "预案类型", 2, 3, isTypeModels);
        String fileName = "应急预案导入模板.xlsx";
        try {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            response.setHeader("Content-Disposition", "attachment;filename=" + "应急预案导入模板.xlsx");
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(response.getOutputStream());
            workbook.write(bufferedOutPut);
            bufferedOutPut.flush();
            bufferedOutPut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final class ExcelSelectListUtil {
        /**
         * firstRow 開始行號 根据此项目，默认为3(下标0开始)
         * lastRow  根据此项目，默认为最大65535
         * firstCol 区域中第一个单元格的列号 (下标0开始)
         * lastCol 区域中最后一个单元格的列号
         * strings 下拉内容
         */

        public static void selectList(Workbook workbook, String name, int firstCol, int lastCol, List<DictModel> modelList) {
            if (CollectionUtil.isNotEmpty(modelList)) {
                Sheet sheet = workbook.getSheetAt(0);
                //将新建的sheet页隐藏掉, 下拉值太多，需要创建隐藏页面
                int sheetTotal = workbook.getNumberOfSheets();
                List<String> collect = modelList.stream().map(DictModel::getText).collect(Collectors.toList());
                String hiddenSheetName = name + "_hiddenSheet";
                Sheet hiddenSheet = workbook.getSheet(hiddenSheetName);
                if (hiddenSheet == null) {
                    hiddenSheet = workbook.createSheet(hiddenSheetName);
                    //写入下拉数据到新的sheet页中
                    for (int i = 0; i < collect.size(); i++) {
                        Row hiddenRow = hiddenSheet.createRow(i);
                        Cell hiddenCell = hiddenRow.createCell(0);
                        hiddenCell.setCellValue(collect.get(i));
                    }
                    workbook.setSheetHidden(sheetTotal, true);
                }

                // 下拉数据
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 65535, firstCol, lastCol);
                //  生成下拉框内容名称
                String strFormula = hiddenSheetName + "!$A$1:$A$65535";
                // 根据隐藏页面创建下拉列表
                XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(DataValidationConstraint.ValidationType.LIST, strFormula);
                XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) hiddenSheet);
                DataValidation validation = dvHelper.createValidation(constraint, cellRangeAddressList);
                //  对sheet页生效
                sheet.addValidationData(validation);
            }
        }
    }

    /**
     * 应急预案导出数据
     * @param request
     * @param response
     * @param emergencyPlanDto
     */
    @Override
    public void exportXls(HttpServletRequest request, HttpServletResponse response, EmergencyPlanDTO emergencyPlanDto) {
        // 封装数据
        List<EmergencyPlanExcelDTO> pageList = this.getinspectionStrategyList(emergencyPlanDto);

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
     * @param emergencyPlanDto
     * @return
     */
    private List<EmergencyPlanExcelDTO> getinspectionStrategyList(EmergencyPlanDTO emergencyPlanDto) {

        List<EmergencyPlanExcelDTO> result =emergencyPlanMapper.selectListNoPage(emergencyPlanDto);
        if (CollUtil.isEmpty(result)) {
            return result;
        }
        // 处置程序
        for (EmergencyPlanExcelDTO emergencyPlanExcelDTO : result) {
            if (ObjectUtil.isEmpty(emergencyPlanExcelDTO)) {
                continue;
            }
            List<EmergencyPlanDisposalProcedureExcelDTO> planDisposalProcedureList = emergencyPlanMapper.selectPlanDisposalProcedureById(emergencyPlanExcelDTO.getId());
            if (CollUtil.isEmpty(planDisposalProcedureList)) {
                continue;
            }
            List<EmergencyPlanMaterialsExcelDTO> planMaterialsList = emergencyPlanMapper.selectPlanMaterialsById(emergencyPlanExcelDTO.getId());
            if (CollUtil.isEmpty(planMaterialsList)) {
                continue;
            }
            emergencyPlanExcelDTO.setPlanDisposalProcedureList(planDisposalProcedureList);
            emergencyPlanExcelDTO.setPlanMaterialsDTOList(planMaterialsList);
        }
        return result;
    }



    @Override
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        // 失败条数
        Integer  errorLines = 0;
        // 标记是否有错误信息
        Boolean errorSign = false;
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
            com.aiurt.boot.plan.controller.RecordExcelListener recordExcelListener =new RecordExcelListener();

            EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO1 = recordExcelListener.getEmergencyPlanImportExcelDTO();
            List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = emergencyPlanImportExcelDTO1.getPlanDisposalProcedureList();
            Iterator<EmergencyPlanDisposalProcedureImportExcelDTO> iterator = planDisposalProcedureList.iterator();
            while (iterator.hasNext()) {
                EmergencyPlanDisposalProcedureImportExcelDTO model = iterator.next();
                boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                if (a) {
                    iterator.remove();
                }
            }
            if (CollUtil.isEmpty(planDisposalProcedureList)) {
                return Result.error("文件导入失败:应急预案处置程序不能为空！");
            }

            // 需要保存的数据
            List<EmergencyPlanDTO> saveData = CollUtil.newArrayList();
            // excel表格数据
            List<EmergencyPlanImportExcelDTO> list = null;
            try {
                // 记录校验得到的错误信息
                StringBuilder errorMessage = new StringBuilder();
                //读取数据
                 EasyExcel.read(file.getInputStream(), EmergencyPlanImportExcelDTO.class, recordExcelListener).sheet().doRead();

                // 空表格直接返回
                if(CollUtil.isEmpty(list)){
                    return imporReturnRes(errorLines, false, failReportUrl,"暂无导入数据");
                }
                // 校验数据
//                for (EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO : list) {
//                    EmergencyPlanDTO emergencyPlanDTO = new EmergencyPlanDTO();
//                    // 校验应急预案
//                    this.checkData(errorMessage, emergencyPlanImportExcelDTO, emergencyPlanDTO);
//                    // 校验应急预案处置程序
//                    this.checkDisposalProcedureCode(errorSign, emergencyPlanImportExcelDTO, emergencyPlanDTO);
//                    // 校验应急预案物资清单
//                    this.checkMaterialsCode(errorSign, emergencyPlanImportExcelDTO, emergencyPlanDTO);
//
//                    if (errorMessage.length() > 0 ) {
//                        if(errorMessage.length() > 0 || errorSign ){
//                            errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
//                            emergencyPlanImportExcelDTO.setEmergencyPlanErrorReason(errorMessage.toString());
//                        }
//                        errorLines++;
//                    } else {
//                        saveData.add(emergencyPlanDTO);
//                    }
//                }

                // 存在错误，错误报告下载
                if (errorLines > 0) {
                    return getErrorExcel(errorLines, list, failReportUrl, type);
                }

                // 保存到系统
                if (CollUtil.isNotEmpty(saveData)) {
                    for (EmergencyPlanDTO saveDatum : saveData) {
                        this.saveAndAdd(saveDatum);
                    }
                    return imporReturnRes(errorLines, true, failReportUrl,"文件导入成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
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
     * @param emergencyPlanImportExcelDTO excel数据
     * @param emergencyPlanDTO       转换成要保存的实体数据
     */
    private void checkData(StringBuilder errorMessage, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        // 空数据不处理
        if (ObjectUtil.isEmpty(emergencyPlanImportExcelDTO)) {
            return;
        }
        // 应急预案必填校验
        requiredCheck(errorMessage, emergencyPlanImportExcelDTO, emergencyPlanDTO);
    }
    private void requiredCheck(StringBuilder errorMessage, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        //获取部门
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String orgCode = loginUser.getOrgCode();
        //编制部门
        emergencyPlanDTO.setOrgCode(orgCode);
        //预案版本默认值
        emergencyPlanDTO.setEmergencyPlanVersion("1.0");
        // 应急预案类型转换
        HashMap<String, Integer> checkMap = CollUtil.newHashMap();
        checkMap.put("综合应急预案", 1);
        checkMap.put("专项应急预案", 2);
        checkMap.put("现场处置方案", 3);
        if(ObjectUtil.isEmpty(emergencyPlanImportExcelDTO.getEmergencyPlanType())){
              errorMessage.append("预案类型必须填写，");
          }else {
            Integer isConfirm = checkMap.get(emergencyPlanImportExcelDTO.getEmergencyPlanType());
            if (ObjectUtil.isNotEmpty(isConfirm)) {
                emergencyPlanDTO.setEmergencyPlanType(isConfirm);
            } else {
                errorMessage.append("应急预案类型格式错误，");
            }
        }
        if(ObjectUtil.isEmpty(emergencyPlanImportExcelDTO.getEmergencyPlanName())){
            errorMessage.append("应急预案名称必须填写，");
        }
        if(ObjectUtil.isEmpty(emergencyPlanImportExcelDTO.getEmergencyTeamId())){
            errorMessage.append("应急队伍必须填写，");
        }else{
            String emergencyTeamId = emergencyPlanImportExcelDTO.getEmergencyTeamId();
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
                emergencyPlanDTO.setEmergencyTeamId(teamList);
            } else {
                errorMessage.append("不存在这个队伍!");
            }

        }

    }

    private Boolean checkDisposalProcedureCode(Boolean errorSign, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        if (ObjectUtil.isEmpty(emergencyPlanImportExcelDTO)
                || CollUtil.isEmpty(emergencyPlanImportExcelDTO.getPlanDisposalProcedureList())) {
            return false;
        }
        List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = emergencyPlanImportExcelDTO.getPlanDisposalProcedureList();
        // 封装处置程序到应急预案实体
        List<EmergencyPlanDisposalProcedure> disposalProcedureList = CollUtil.newArrayList();

        // 检修标准是否存在系统
        for (EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO : planDisposalProcedureList) {

            // 错误信息
            StringBuilder errorMessage = new StringBuilder();
            if(ObjectUtil.isEmpty(emergencyPlanDisposalProcedureImportExcelDTO.getOrgCode())){
                errorMessage.append("处置部门不能为空!");
            }
            if(ObjectUtil.isEmpty(emergencyPlanDisposalProcedureImportExcelDTO.getRoleId())){
                errorMessage.append("处置角色不能为空!");
            }
            if(ObjectUtil.isEmpty(emergencyPlanDisposalProcedureImportExcelDTO.getDisposalProcedureContent())){
                errorMessage.append("处置内容不能为空!");
            }

            if (errorMessage.length() > 0) {
                // 截取字符
                errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                emergencyPlanDisposalProcedureImportExcelDTO.setErrorReason(errorMessage.toString());
                errorSign = true;
            } else {
                EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure = new EmergencyPlanDisposalProcedure();
                emergencyPlanDisposalProcedure.setOrgCode(emergencyPlanDisposalProcedureImportExcelDTO.getOrgCode());
                emergencyPlanDisposalProcedure.setRoleId(emergencyPlanDisposalProcedureImportExcelDTO.getRoleId());
                emergencyPlanDisposalProcedure.setDisposalProcedureContent(emergencyPlanDisposalProcedureImportExcelDTO.getDisposalProcedureContent());
                disposalProcedureList.add(emergencyPlanDisposalProcedure);
            }
        }
        emergencyPlanDTO.setEmergencyPlanDisposalProcedure(disposalProcedureList);
        return errorSign;
    }
    private Boolean checkMaterialsCode(Boolean errorSign, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        return errorSign;
    }

    /**
     * 下载错误清单
     * @param errorLines
     * @param list
     * @param url
     * @param type
     * @return
     * @throws IOException
     */
    private Result<?> getErrorExcel(int errorLines, List<EmergencyPlanImportExcelDTO> list, String url, String type) throws IOException {
        //创建导入失败错误报告,进行模板导出
        org.springframework.core.io.Resource resource = new ClassPathResource("/templates/InspectionStyError.xls");
        InputStream resourceAsStream = resource.getInputStream();

        //2.获取临时文件
        File fileTemp = new File("/templates/InspectionStyError.xls");
        try {
            //将读取到的类容存储到临时文件中，后面就可以用这个临时文件访问了
            FileUtils.copyInputStreamToFile(resourceAsStream, fileTemp);

            String path = fileTemp.getAbsolutePath();
            TemplateExportParams exportParams = new TemplateExportParams(path);

            // 封装数据
            Map<String, Object> errorMap = handleData(list);

            // 将数据填入表格
            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook = ExcelExportUtil.exportExcel(sheetsMap, exportParams);

            // 合并数据
            // size从第6行开始合并，对应模板
            int size = 5;
            for (EmergencyPlanImportExcelDTO deviceModel : list) {
                for (int i = 0; i <= 9; i++) {
                    //合并单元格
                    PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getPlanDisposalProcedureList().size() - 1, i, i);
                }
//                PoiMergeCellUtil.addMergedRegion(workbook.getSheetAt(0), size, size + deviceModel.getPlanDisposalProcedureList()).size() - 1, 14, 14);
                size = size + deviceModel.getPlanDisposalProcedureList().size();
            }

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
    private Map<String, Object> handleData(List<EmergencyPlanImportExcelDTO> list) {
        Map<String, Object> errorMap = CollUtil.newHashMap();
        List<Map<String, Object>> listMap = CollUtil.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> lm = CollUtil.newHashMap();
            EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO = list.get(i);
            if (ObjectUtil.isEmpty(emergencyPlanImportExcelDTO)) {
                continue;
            }
            if (CollUtil.isNotEmpty(emergencyPlanImportExcelDTO.getPlanDisposalProcedureList())) {
                List<EmergencyPlanDisposalProcedureImportExcelDTO> inspectionExcelDTOList = emergencyPlanImportExcelDTO.getPlanDisposalProcedureList();
                for (EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO : inspectionExcelDTOList) {
                    lm = CollUtil.newHashMap();
                    //错误报告获取信息
                    listMap.add(lm);
                }
            } else {
                //错误报告获取信息
                listMap.add(lm);
            }

        }
        errorMap.put("maplist", listMap);
        return errorMap;
    }


    /**
     * 校验必填信息
     *
     * @param emergencyPlanDTO
     */
    private void check(EmergencyPlanDTO emergencyPlanDTO) {

        if (ObjectUtil.isEmpty(emergencyPlanDTO)) {
            throw new AiurtBootException("必填参数为空");
        }
    }
}
