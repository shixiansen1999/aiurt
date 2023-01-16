package com.aiurt.boot.plan.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.util.PoiMergeCellUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.aiurt.boot.materials.service.IEmergencyMaterialsCategoryService;
import com.aiurt.boot.materials.service.IEmergencyMaterialsService;
import com.aiurt.boot.plan.constant.EmergencyPlanConstant;
import com.aiurt.boot.plan.controller.RecordExcelListener;
import com.aiurt.boot.plan.dto.*;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.EmergencyPlanMapper;
import com.aiurt.boot.plan.service.*;
import com.aiurt.boot.plan.vo.EmergencyPlanExportExcelVO;
import com.aiurt.boot.plan.vo.EmergencyPlanMaterialsExportExcelVO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.aiurt.boot.team.entity.EmergencyTrainingProcessRecord;
import com.aiurt.boot.team.entity.EmergencyTrainingRecordAtt;
import com.aiurt.boot.team.model.ProcessRecordModel;
import com.aiurt.boot.team.service.IEmergencyTeamService;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.MinioUtil;
import com.aiurt.common.util.XlsUtil;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.FlowTaskCompleteCommentDTO;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.StartBpmnImportDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.util.SpringContextUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
import java.util.zip.ZipOutputStream;

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
    private IEmergencyMaterialsService emergencyMaterialsService;
    @Autowired
    private IEmergencyMaterialsCategoryService emergencyMaterialsCategoryService;
    @Autowired
    @Lazy
    private IEmergencyPlanService emergencyPlanService;
    @Autowired
    private EmergencyPlanMapper emergencyPlanMapper;

    @Autowired
    private FlowBaseApi flowBaseApi;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;
    @Value("${jeecg.path.errorExcelUpload}")
    private String errorExcelUpload;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<EmergencyPlan> queryPageList(Page<EmergencyPlan> page, EmergencyPlanQueryDTO emergencyPlanQueryDto) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<EmergencyPlan> pageList = emergencyPlanMapper.queryPageList(page, emergencyPlanQueryDto);
        return pageList;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IPage<EmergencyPlan> queryWorkToDo(Page<EmergencyPlan> page, EmergencyPlanQueryDTO emergencyPlanQueryDto) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录，请登录后操作！");
        }
        IPage<EmergencyPlan> pageList = emergencyPlanMapper.queryWorkToDo(page, emergencyPlanQueryDto,loginUser.getUsername());
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
        //查询预案变更次数
        int size = 0;
        String oldPlanId = emergencyPlanDto.getOldPlanId();
        //获取根节点id
        if(StrUtil.isNotBlank(oldPlanId)){
            String firstPlanId = StrUtil.splitTrim(oldPlanId, "/").get(0);
            size = emergencyPlanService.lambdaQuery().like(EmergencyPlan::getOldPlanId, firstPlanId).list().size();
        }else{
            size = emergencyPlanService.lambdaQuery().like(EmergencyPlan::getOldPlanId, emergencyPlanDto.getId()).list().size();

        }

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
        newEmergencyPlanDto.setChangeCount(size);
        //变更版本：原版本+变更次数+1
        double version = (1.0 + size + 1);
        newEmergencyPlanDto.setEmergencyPlanVersion(String.valueOf(version));
        //变更后保存父级预案id
        if(StrUtil.isEmpty(oldPlanId)){
            newEmergencyPlanDto.setOldPlanId(emergencyPlanDto.getId());
        }else{
            newEmergencyPlanDto.setOldPlanId(oldPlanId+"/"+emergencyPlanDto.getId());
        }
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
        emergencyPlan.setDelFlag(EmergencyPlanConstant.DEL_FLAG1);
        this.updateById(emergencyPlan);

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

        //查询预案变更次数
        int size = 0;
        String oldPlanId = plan.getOldPlanId();
        //获取根节点id
        if(StrUtil.isNotBlank(oldPlanId)){
            String firstPlanId = StrUtil.splitTrim(oldPlanId, "/").get(0);
            size = emergencyPlanService.lambdaQuery().like(EmergencyPlan::getOldPlanId, firstPlanId).list().size();
        }else{
            size = emergencyPlanService.lambdaQuery().like(EmergencyPlan::getOldPlanId, plan.getId()).list().size();

        }
        planDto.setChangeCount(size);

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
                    //获取父级id
               String oldPlanId1 = emergencyPlan.getOldPlanId();
               List<String> parentList = StrUtil.splitTrim(oldPlanId1, "/");
               String lastPlanId = parentList.get(parentList.size() - 1);

                    List<EmergencyPlan> list = emergencyPlanService.lambdaQuery()
                            .eq(EmergencyPlan::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                            .eq(EmergencyPlan::getId, lastPlanId).list();
                    for (EmergencyPlan plan : list) {
                        plan.setStatus(EmergencyPlanConstant.STOPPED);
                        this.updateById(plan);
                    }
                }
                Date nowDate = DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                emergencyPlan.setApprovedTime(nowDate);
                break;
            default:
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
        ExcelSelectListUtil.selectList(workbook, "预案类型", 0, 3, isTypeModels);
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
                CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 2, firstCol, lastCol);
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
     * @param response
     * @param id
     */
    @Override
    public void exportXls(HttpServletResponse response, String id) {
        EmergencyPlanExportExcelVO emergencyPlanExportExcelVO = emergencyPlanMapper.queryById(id);
        HashMap<String, String> checkMap = CollUtil.newHashMap();
        checkMap.put("1","综合应急预案");
        checkMap.put("2","专项应急预案");
        checkMap.put("3","现场处置方案");
        String isConfirm = checkMap.get(emergencyPlanExportExcelVO.getEmergencyPlanType());
        emergencyPlanExportExcelVO.setEmergencyPlanType(isConfirm);

        StringBuffer teamName = new StringBuffer();
        List<EmergencyPlanTeam> planTeamList = emergencyPlanTeamService.lambdaQuery()
                .eq(EmergencyPlanTeam::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanTeam::getEmergencyPlanId, id).list();
        for (EmergencyPlanTeam emergencyPlanTeam : planTeamList) {
            String emergencyTeamId = emergencyPlanTeam.getEmergencyTeamId();
            List<EmergencyTeam> teamList = emergencyTeamService.lambdaQuery()
                    .eq(EmergencyTeam::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                    .eq(EmergencyTeam::getId, emergencyTeamId).list();
            for (EmergencyTeam emergencyTeam : teamList) {
                String emergencyTeamname = emergencyTeam.getEmergencyTeamname();
                teamName.append(emergencyTeamname);
                teamName.append(";");
            }
        }
        emergencyPlanExportExcelVO.setEmergencyTeamId(String.valueOf(teamName));


        // 查询对应的应急预案处置程序
        List<EmergencyPlanDisposalProcedure> procedureList = emergencyPlanDisposalProcedureService.lambdaQuery()
                .eq(EmergencyPlanDisposalProcedure::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanDisposalProcedure::getEmergencyPlanId, id).list();
        this.disposalProcedureTranslate(procedureList);
        emergencyPlanExportExcelVO.setPlanDisposalProcedureList(procedureList);

        //查询应急预案附件
        List<EmergencyPlanAtt> planAttList = emergencyPlanAttService.lambdaQuery()
                .eq(EmergencyPlanAtt::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanAtt::getEmergencyPlanId, id).list();

        //组装物资数据
        List<EmergencyPlanMaterialsExportExcelVO> planMaterialsList = new ArrayList<>();
        List<EmergencyPlanMaterials> planMaterials = emergencyPlanMaterialsService.lambdaQuery()
                .eq(EmergencyPlanMaterials::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                .eq(EmergencyPlanMaterials::getEmergencyPlanId, id).list();
        if(CollUtil.isNotEmpty(planMaterials)){
            for (EmergencyPlanMaterials planMaterial : planMaterials) {
                EmergencyPlanMaterialsExportExcelVO emergencyPlanMaterialsExportExcelVO = new EmergencyPlanMaterialsExportExcelVO();
                String materialsCode = planMaterial.getMaterialsCode();
                Integer materialsNumber = planMaterial.getMaterialsNumber();
                emergencyPlanMaterialsExportExcelVO.setMaterialsCode(materialsCode);
                emergencyPlanMaterialsExportExcelVO.setMaterialsNumber(String.valueOf(materialsNumber));
                //根据物资code查询物资信息
                List<EmergencyMaterials> materialsList = emergencyMaterialsService.lambdaQuery().eq(EmergencyMaterials::getDelFlag, EmergencyPlanConstant.DEL_FLAG0)
                        .eq(EmergencyMaterials::getMaterialsCode, materialsCode).list();
                if(CollUtil.isNotEmpty(materialsList)){
                    for (EmergencyMaterials emergencyMaterials : materialsList) {
                        String categoryCode = emergencyMaterials.getCategoryCode();
                        //查询物资分类信息
                        List<EmergencyMaterialsCategory> list = emergencyMaterialsCategoryService.lambdaQuery()
                                .eq(EmergencyMaterialsCategory::getCategoryCode, categoryCode)
                                .eq(EmergencyMaterialsCategory::getDelFlag,EmergencyPlanConstant.DEL_FLAG0)
                                .list();
                        for (EmergencyMaterialsCategory emergencyMaterialsCategory : list) {
                            String categoryName = emergencyMaterialsCategory.getCategoryName();
                            emergencyPlanMaterialsExportExcelVO.setCategoryName(categoryName);
                        }
                        String materialsName = emergencyMaterials.getMaterialsName();
                        String unit = emergencyMaterials.getUnit();
                        emergencyPlanMaterialsExportExcelVO.setMaterialsName(materialsName);
                        emergencyPlanMaterialsExportExcelVO.setUnit(unit);
                    }
                    planMaterialsList.add(emergencyPlanMaterialsExportExcelVO);
                }
            }

        }
        emergencyPlanExportExcelVO.setPlanMaterialsList(planMaterialsList);


        try {
            TemplateExportParams exportParams = XlsUtil.getExcelModel("templates/emergencyPlanExport.xlsx");
            Map<String, Object> errorMap = CollUtil.newHashMap();
            List<Map<String, String>> mapList = CollUtil.newArrayList();
            List<Map<String, String>> mapList2 = CollUtil.newArrayList();

            //应急预案
            errorMap.put("emergencyPlanType", emergencyPlanExportExcelVO.getEmergencyPlanType());
            errorMap.put("emergencyPlanName", emergencyPlanExportExcelVO.getEmergencyPlanName());
            errorMap.put("emergencyTeam", emergencyPlanExportExcelVO.getEmergencyTeamId());
            errorMap.put("keyWord", emergencyPlanExportExcelVO.getKeyWord());
            errorMap.put("emergencyPlanContent", emergencyPlanExportExcelVO.getEmergencyPlanContent());
            //处置程序
            List<EmergencyPlanDisposalProcedure> planDisposalProcedureList = emergencyPlanExportExcelVO.getPlanDisposalProcedureList();
            if (CollUtil.isNotEmpty(planDisposalProcedureList)) {
                for (int i = 0; i < planDisposalProcedureList.size(); i++) {
                    EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure = planDisposalProcedureList.get(i);
                    Map<String, String> map = new HashMap<>();
                    map.put("sort", Convert.toStr(i+1));
                    map.put("orgName", emergencyPlanDisposalProcedure.getOrgName());
                    map.put("roleName", emergencyPlanDisposalProcedure.getRoleName());
                    map.put("disposalProcedureContent", emergencyPlanDisposalProcedure.getDisposalProcedureContent());
                    mapList.add(map);
                }
            }
            //预案物资
            List<EmergencyPlanMaterialsExportExcelVO> planMaterialsList1 = emergencyPlanExportExcelVO.getPlanMaterialsList();
            if(CollUtil.isNotEmpty(planMaterialsList1)){
                for (int i = 0; i < planMaterialsList.size(); i++) {
                    EmergencyPlanMaterialsExportExcelVO emergencyPlanMaterialsExportExcelVO = planMaterialsList1.get(i);
                    Map<String, String> map2 = new HashMap<>();
                    map2.put("sort", Convert.toStr(i+1));
                    map2.put("categoryName", emergencyPlanMaterialsExportExcelVO.getCategoryName());
                    map2.put("materialsCode", emergencyPlanMaterialsExportExcelVO.getMaterialsCode());
                    map2.put("materialsName", emergencyPlanMaterialsExportExcelVO.getMaterialsName());
                    map2.put("materialsNumber", emergencyPlanMaterialsExportExcelVO.getMaterialsNumber());
                    map2.put("unit", emergencyPlanMaterialsExportExcelVO.getUnit());
                    mapList2.add(map2);
                }
            }
            errorMap.put("maplist", mapList);
            errorMap.put("maplist2", mapList2);

            Map<Integer, Map<String, Object>> sheetsMap = new HashMap<>();
            sheetsMap.put(0, errorMap);
            Workbook workbook =  ExcelExportUtil.exportExcel(sheetsMap, exportParams);
            int size = procedureList.size();
            Sheet sheet = workbook.getSheetAt(0);
            for (int j = 0 ;j < size; j++) {
                CellRangeAddress cellAddresses = new CellRangeAddress(10+j,10+j,4,6);
                //合并
                sheet.addMergedRegion(cellAddresses);
                //合并后设置下边框
                RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet);
            }

            //打包成压缩包导出
            String fileName = "应急预案台账.zip";
            response.setContentType("application/zip");
            response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            // 压缩输出流,包装流,将临时文件输出流包装成压缩流,将所有文件输出到这里,打成zip包
            ZipOutputStream zipOut = new ZipOutputStream(outputStream);

            for (EmergencyPlanAtt emergencyPlanAtt : planAttList) {
                String attName = null;
                String filePath = null;
                String path = emergencyPlanAtt.getPath();
                filePath = StrUtil.subBefore(path, "?", false);

                filePath = filePath.replace("..", "").replace("../", "");
                if (filePath.endsWith(SymbolConstant.COMMA)) {
                    filePath = filePath.substring(0, filePath.length() - 1);
                }

                SysAttachment sysAttachment = sysBaseApi.getFilePath(filePath);
                InputStream inputStream = null;

                if (Objects.isNull(sysAttachment)) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new AiurtBootException("文件不存在..");
                    }
                    if (StrUtil.isBlank(emergencyPlanAtt.getName())) {
                        attName = file.getName();
                    } else {
                        attName = emergencyPlanAtt.getName();
                    }
                    inputStream = new BufferedInputStream(new FileInputStream(filePath));

                    XlsUtil.outZip(inputStream,attName,zipOut);
                    //关闭流
                    inputStream.close();

                }else {
                    if (StrUtil.equalsIgnoreCase("minio",sysAttachment.getType())) {
                        inputStream = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
                    }else {
                        String imgPath = upLoadPath + File.separator + sysAttachment.getFilePath();
                        File file = new File(imgPath);
                        if (!file.exists()) {
                            response.setStatus(404);
                            throw new RuntimeException("文件[" + imgPath + "]不存在..");
                        }
                        inputStream = new BufferedInputStream(new FileInputStream(imgPath));
                    }
                    XlsUtil.outZip(inputStream,sysAttachment.getFileName(),zipOut);
                    //关闭流
                    inputStream.close();
                }

            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] barray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(barray);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
            String file = "应急预案台账.xlsx";
            XlsUtil.outZip(bufferedInputStream,file,zipOut);
            //关闭流
            is.close();
            bufferedInputStream.close();

            zipOut.flush();
            // 压缩完成后,关闭压缩流
            zipOut.close();

            outputStream.flush();
            outputStream.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        //成功条数
        Integer successLines = 0;
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
                return imporReturnRes(errorLines,successLines, false, failReportUrl,"文件导入失败，文件类型不对");
            }
            //读取数据监听
            RecordExcelListener recordExcelListener =new RecordExcelListener();
            //读取数据
            try {
                EasyExcel.read(file.getInputStream(), RecordData.class, recordExcelListener).sheet().doRead();
            } catch (IOException e) {
                e.printStackTrace();
            }
           //判断读取的数据是否有空行
            EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO = recordExcelListener.getEmergencyPlanImportExcelDTO();
            List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = emergencyPlanImportExcelDTO.getPlanDisposalProcedureList();
            List<EmergencyPlanMaterialsImportExcelDTO> planMaterialsList = emergencyPlanImportExcelDTO.getPlanMaterialsList();
            if(CollUtil.isNotEmpty(planDisposalProcedureList)){
                Iterator<EmergencyPlanDisposalProcedureImportExcelDTO> iterator = planDisposalProcedureList.iterator();
                if(CollUtil.isNotEmpty(iterator)){
                    while (iterator.hasNext()) {
                        EmergencyPlanDisposalProcedureImportExcelDTO model = iterator.next();
                        boolean a = XlsUtil.checkObjAllFieldsIsNull(model);
                        if (a) {
                            iterator.remove();
                        }
                    }
                }
                if (CollUtil.isEmpty(planDisposalProcedureList)) {
                    return Result.error("文件导入失败:应急预案处置程序不能为空！");
                }
            }
            //判断物资是否读取空数据
            if(CollUtil.isNotEmpty(planMaterialsList)){
                Iterator<EmergencyPlanMaterialsImportExcelDTO> iterator = planMaterialsList.iterator();
                if(CollUtil.isNotEmpty(iterator)){
                    while (iterator.hasNext()) {
                        EmergencyPlanMaterialsImportExcelDTO model = iterator.next();
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
                    EmergencyPlanDTO emergencyPlanDTO = new EmergencyPlanDTO();
                    // 校验应急预案
                    this.checkData(errorMessage, emergencyPlanImportExcelDTO, emergencyPlanDTO);
                    // 校验应急预案处置程序
                    errorSign=this.checkDisposalProcedureCode(errorSign, emergencyPlanImportExcelDTO, emergencyPlanDTO);
                   // 校验应急预案物资清单
                    errorSign2=this.checkMaterialsCode(errorSign2, emergencyPlanImportExcelDTO, emergencyPlanDTO);

                    if (errorMessage.length() > 0 || errorSign || errorSign2) {
                        if(errorMessage.length() > 0  ){
                            errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                            emergencyPlanImportExcelDTO.setEmergencyPlanErrorReason(errorMessage.toString());
                        }
                        errorLines++;
                    }else{
                        successLines++;
                    }

            // 存在错误，错误报告下载
            if (errorLines > 0) {
                try {
                    return getErrorExcel(errorLines,successLines, emergencyPlanImportExcelDTO, failReportUrl, type);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                // 校验通过，保存到系统
                if (ObjectUtil.isNotEmpty(emergencyPlanDTO)) {
                    //插入数据库，并获取预案id
                    String businessKey = this.startProcess(emergencyPlanDTO);
                    //获取登录人信息
                    LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    String userName = loginUser.getUsername();
                    //导入实体转换成Map
                    Map<String, Object> busData = BeanUtil.beanToMap(emergencyPlanDTO);
                    //创建流程实体
                    StartBpmnImportDTO startBpmnImportDTO = new StartBpmnImportDTO();
                    startBpmnImportDTO.setModelKey("emergency_plan");
                    startBpmnImportDTO.setUserName(userName);
                    startBpmnImportDTO.setBusData(busData);
                    startBpmnImportDTO.setBusinessKey(businessKey);
                    //导入数据走流程
                    flowBaseApi.startBpmnWithImport(startBpmnImportDTO);
                    return imporReturnRes(errorLines,successLines, true, failReportUrl,"文件导入成功");
                }
            }
        }
        return imporReturnRes(errorLines,successLines,false, failReportUrl,"暂无导入数据");
    }

    /**
     * 检修策略导入统一返回格式
     * @param errorLines 错误条数
     * @param isSucceed 是否成功
     * @param failReportUrl 错误报告下载地址
     * @param message 提示信息
     * @return
     */
    public static Result<?> imporReturnRes(int errorLines,int successLines, boolean isSucceed, String failReportUrl,String message) {
        JSONObject result = new JSONObject(5);
        result.put("isSucceed", isSucceed);
        result.put("errorCount", errorLines);
        result.put("successCount", successLines);
        result.put("failReportUrl", failReportUrl);
        int totalCount = successLines + errorLines;
        result.put("totalCount", totalCount);
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
        }else{
            emergencyPlanDTO.setEmergencyPlanName(emergencyPlanImportExcelDTO.getEmergencyPlanName());
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
        emergencyPlanDTO.setKeyWord(emergencyPlanImportExcelDTO.getKeyWord());
        emergencyPlanDTO.setEmergencyPlanContent(emergencyPlanImportExcelDTO.getEmergencyPlanContent());

    }

    private Boolean checkDisposalProcedureCode(Boolean errorSign, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        if (ObjectUtil.isEmpty(emergencyPlanImportExcelDTO)
                || CollUtil.isEmpty(emergencyPlanImportExcelDTO.getPlanDisposalProcedureList())) {
            return false;
        }
        List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = emergencyPlanImportExcelDTO.getPlanDisposalProcedureList();
        // 封装处置程序到应急预案实体
        List<EmergencyPlanDisposalProcedure> disposalProcedureList = CollUtil.newArrayList();

        //校验处置程序
        for (EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO : planDisposalProcedureList) {

            // 错误信息
            StringBuilder errorMessage = new StringBuilder();
            EmergencyPlanDisposalProcedure emergencyPlanDisposalProcedure = new EmergencyPlanDisposalProcedure();
            if(ObjectUtil.isNotEmpty(emergencyPlanDisposalProcedureImportExcelDTO.getOrgName())){
                String orgName = emergencyPlanDisposalProcedureImportExcelDTO.getOrgName();
                String orgCode = emergencyPlanMapper.selectDepartCode(orgName);
                if(ObjectUtil.isNotEmpty(orgCode)){
                    emergencyPlanDisposalProcedure.setOrgCode(orgCode);
                }else{
                    errorMessage.append("不存在这个部门!");
                }
            }
            if(ObjectUtil.isNotEmpty(emergencyPlanDisposalProcedureImportExcelDTO.getRoleName())){
                String roleName = emergencyPlanDisposalProcedureImportExcelDTO.getRoleName();
                String roleId = emergencyPlanMapper.selectRoleId(roleName);
                if(ObjectUtil.isNotEmpty(roleId)){
                    emergencyPlanDisposalProcedure.setRoleId(roleId);
                }else{
                    errorMessage.append("不存在这个角色!");
                }
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
                emergencyPlanDisposalProcedure.setDisposalProcedureContent(emergencyPlanDisposalProcedureImportExcelDTO.getDisposalProcedureContent());
                disposalProcedureList.add(emergencyPlanDisposalProcedure);
            }
        }
        emergencyPlanDTO.setEmergencyPlanDisposalProcedure(disposalProcedureList);
        return errorSign;
    }
    private Boolean checkMaterialsCode(Boolean errorSign, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, EmergencyPlanDTO emergencyPlanDTO) {
        if (ObjectUtil.isEmpty(emergencyPlanImportExcelDTO)
                || CollUtil.isEmpty(emergencyPlanImportExcelDTO.getPlanMaterialsList())) {
            return false;
        }
        //导入填写的物资信息
        List<EmergencyPlanMaterialsImportExcelDTO> planMaterialsList = emergencyPlanImportExcelDTO.getPlanMaterialsList();
        // 封装处置程序到应急预案实体
        List<EmergencyPlanMaterials> materialList = CollUtil.newArrayList();

        for (EmergencyPlanMaterialsImportExcelDTO emergencyPlanMaterialsImportExcelDTO : planMaterialsList) {
            // 错误信息
            StringBuilder errorMessage = new StringBuilder();
            EmergencyPlanMaterials emergencyPlanMaterials = new EmergencyPlanMaterials();
            if(ObjectUtil.isEmpty(emergencyPlanMaterialsImportExcelDTO.getMaterialsCode())){
                errorMessage.append("应急物资编号不能为空!");
            }else{
                String materialsCode = emergencyPlanMaterialsImportExcelDTO.getMaterialsCode();
                LambdaQueryWrapper<EmergencyMaterials> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(EmergencyMaterials::getMaterialsCode,materialsCode);
                Long aLong = emergencyMaterialsService.getBaseMapper().selectCount(lambdaQueryWrapper);
                if(aLong >= 1){
                    emergencyPlanMaterials.setMaterialsCode(materialsCode);
                }else{
                    errorMessage.append("不存在该应急物资编号!");
                }
            }

            if (errorMessage.length() > 0) {
                // 截取字符
                errorMessage = errorMessage.deleteCharAt(errorMessage.length() - 1);
                emergencyPlanMaterialsImportExcelDTO.setErrorReason(errorMessage.toString());
                errorSign = true;
            } else {
                emergencyPlanMaterials.setMaterialsCode(emergencyPlanMaterialsImportExcelDTO.getMaterialsCode());
                if(StrUtil.isNotEmpty(emergencyPlanMaterialsImportExcelDTO.getMaterialsNumber())){
                    emergencyPlanMaterials.setMaterialsNumber(Integer.valueOf(emergencyPlanMaterialsImportExcelDTO.getMaterialsNumber()));
                }
                materialList.add(emergencyPlanMaterials);
            }
        }
        emergencyPlanDTO.setEmergencyPlanMaterials(materialList);
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
    private Result<?> getErrorExcel(int errorLines,int successLines, EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO, String url, String type) throws IOException {
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
            Map<String, Object> errorMap = handleData(emergencyPlanImportExcelDTO);

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

        return imporReturnRes(errorLines,successLines, false, url,"文件导入失败，数据有错误");
    }

    @NotNull
    private Map<String, Object> handleData(EmergencyPlanImportExcelDTO emergencyPlanImportExcelDTO) {
        Map<String, Object> errorMap = CollUtil.newHashMap();
        List<Map<String, String>> mapList = CollUtil.newArrayList();
        List<Map<String, String>> mapList2 = CollUtil.newArrayList();

        //应急预案
        errorMap.put("emergencyPlanType", emergencyPlanImportExcelDTO.getEmergencyPlanType());
        errorMap.put("emergencyPlanName", emergencyPlanImportExcelDTO.getEmergencyPlanName());
        errorMap.put("emergencyTeam", emergencyPlanImportExcelDTO.getEmergencyTeamId());
        errorMap.put("keyWord", emergencyPlanImportExcelDTO.getKeyWord());
        errorMap.put("emergencyPlanContent", emergencyPlanImportExcelDTO.getEmergencyPlanContent());
        errorMap.put("planErrorReason", emergencyPlanImportExcelDTO.getEmergencyPlanErrorReason());
        //处置程序
        List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = emergencyPlanImportExcelDTO.getPlanDisposalProcedureList();
        if (CollUtil.isNotEmpty(planDisposalProcedureList)) {
            for (int i = 0; i < planDisposalProcedureList.size(); i++) {
                EmergencyPlanDisposalProcedureImportExcelDTO emergencyPlanDisposalProcedureImportExcelDTO = planDisposalProcedureList.get(i);
                Map<String, String> map = new HashMap<>();
                map.put("sort", Convert.toStr(i+1));
                map.put("orgName", emergencyPlanDisposalProcedureImportExcelDTO.getOrgName());
                map.put("roleName", emergencyPlanDisposalProcedureImportExcelDTO.getRoleName());
                map.put("disposalProcedureContent", emergencyPlanDisposalProcedureImportExcelDTO.getDisposalProcedureContent());
                map.put("mistake", emergencyPlanDisposalProcedureImportExcelDTO.getErrorReason());
                mapList.add(map);
            }
        }
        //预案物资
        List<EmergencyPlanMaterialsImportExcelDTO> planMaterialsList = emergencyPlanImportExcelDTO.getPlanMaterialsList();
        if(CollUtil.isNotEmpty(planMaterialsList)){
            for (int i = 0; i < planMaterialsList.size(); i++) {
                EmergencyPlanMaterialsImportExcelDTO emergencyPlanMaterialsImportExcelDTO = planMaterialsList.get(i);
                Map<String, String> map2 = new HashMap<>();
                map2.put("sort", Convert.toStr(i+1));
                map2.put("categoryName", emergencyPlanMaterialsImportExcelDTO.getCategoryName());
                map2.put("materialsCode", emergencyPlanMaterialsImportExcelDTO.getMaterialsCode());
                map2.put("materialsName", emergencyPlanMaterialsImportExcelDTO.getMaterialsName());
                map2.put("materialsNumber", emergencyPlanMaterialsImportExcelDTO.getMaterialsNumber());
                map2.put("unit", emergencyPlanMaterialsImportExcelDTO.getUnit());
                map2.put("maMistake", emergencyPlanMaterialsImportExcelDTO.getErrorReason());
                mapList2.add(map2);
            }
        }
        errorMap.put("maplist", mapList);
        errorMap.put("maplist2", mapList2);
        return errorMap;
    }

}
