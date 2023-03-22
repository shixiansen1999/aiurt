package com.aiurt.modules.faultanalysisreport.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.common.api.IFlowableBaseUpdateStatusService;
import com.aiurt.modules.common.entity.RejectFirstUserTaskEntity;
import com.aiurt.modules.common.entity.UpdateStateEntity;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.faultanalysisreport.constants.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fault_analysis_report
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class FaultAnalysisReportServiceImpl extends ServiceImpl<FaultAnalysisReportMapper, FaultAnalysisReport> implements IFaultAnalysisReportService, IFlowableBaseUpdateStatusService {

    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;
    @Autowired
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private IFaultKnowledgeBaseService faultKnowledgeBaseService;
    @Autowired
    private FaultMapper faultMapper;
    @Autowired
    private FlowBaseApi flowBaseApi;
    @Override
    public IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport) {
        //获取权限查询的数据集合
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        List<Fault> faults = faultMapper.selectList(queryWrapper);
        List<String> ids = faults.stream().map(Fault::getId).distinct().collect(Collectors.toList());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        faultAnalysisReport.setCreateBy(sysUser.getUsername());
        if (CollUtil.isEmpty(ids)) {
            return page.setRecords(new ArrayList<>());
        }

        List<FaultAnalysisReport> faultAnalysisReports = faultAnalysisReportMapper.readAll(page, faultAnalysisReport,ids,sysUser.getUsername());

        //解决不是审核人去除审核按钮
        if(CollUtil.isNotEmpty(faultAnalysisReports)){
            for (FaultAnalysisReport report : faultAnalysisReports) {
                TaskInfoDTO taskInfoDTO = flowBaseApi.viewRuntimeTaskInfo(report.getProcessInstanceId(), report.getTaskId());
                List<ActOperationEntity> operationList = taskInfoDTO.getOperationList();
                //operationList为空，没有审核按钮
                if(CollUtil.isNotEmpty(operationList)){
                    report.setHaveButton(true);
                }else{
                    report.setHaveButton(false);
                }
                //当前登录人不是创建人，则为false
                if(report.getCreateBy().equals(sysUser.getUsername())){
                    report.setIsCreateUser(true);
                }else{
                    report.setIsCreateUser(false);
                }
            }
        }
        String asc = "asc";
        if (asc.equals(faultAnalysisReport.getOrder())) {
            List<FaultAnalysisReport> result = faultAnalysisReports.stream().sorted(Comparator.comparing(FaultAnalysisReport::getCreateTime)).collect(Collectors.toList());
            return page.setRecords(result);
        }
        return page.setRecords(faultAnalysisReports);
    }

    @Override
    public FaultAnalysisReport readOne(String id,String faultCode) {
        return faultAnalysisReportMapper.readOne(id,faultCode);
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        //查询已经被引用的故障
        List<String> faultCodes = faultAnalysisReportMapper.getFaultCode();
        List<FaultDTO> faults = faultMapper.getFault(page, faultDTO,faultCodes);
        return page.setRecords(faults);
    }

    @Override
    public FaultDTO getDetail(String id) {
        //获取故障详情
        FaultDTO faultDTO = faultAnalysisReportMapper.getDetail(id);
        //获取故障分析详情
        LambdaQueryWrapper<FaultAnalysisReport> reportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLambdaQueryWrapper.eq(FaultAnalysisReport::getFaultCode, faultDTO.getCode());
        reportLambdaQueryWrapper.eq(FaultAnalysisReport::getDelFlag, 0);
        FaultAnalysisReport faultAnalysisReport = faultAnalysisReportMapper.selectOne(reportLambdaQueryWrapper);
        faultDTO.setFaultAnalysisReport(faultAnalysisReport);
        //获取故障知识详情
        if (ObjectUtils.isNotEmpty(faultAnalysisReport) && StringUtils.isNotEmpty(faultAnalysisReport.getFaultKnowledgeBaseId())) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.selectById(faultAnalysisReport.getFaultKnowledgeBaseId());
            faultDTO.setFaultKnowledgeBase(faultKnowledgeBase);
            faultDTO.setDeviceTypeCode(faultKnowledgeBase.getDeviceTypeCode());
            faultDTO.setMaterialCode(faultKnowledgeBase.getMaterialCode());
            faultDTO.setFaultPhenomenon(faultKnowledgeBase.getKnowledgeBaseTypeCode());
        }else {
            FaultKnowledgeBase faultKnowledgeBase1 = new FaultKnowledgeBase();
            faultKnowledgeBase1.setFaultPhenomenon(faultDTO.getSymptoms());
            faultKnowledgeBase1.setSolution(faultDTO.getSolution());
            faultDTO.setFaultKnowledgeBase(faultKnowledgeBase1);

        }
        return faultDTO;
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if ( getRole()) {return Result.error("没有权限");}
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultAnalysisReport faultAnalysisReport = new FaultAnalysisReport();
        faultAnalysisReport.setId(id);
        faultAnalysisReport.setApprovedRemark(approvedRemark);
        faultAnalysisReport.setApprovedResult(approvedResult);
        faultAnalysisReport.setApprovedTime(new Date());
        faultAnalysisReport.setApprovedUserName(sysUser.getUsername());
        //修改知识库状态
        String faultKnowledgeBaseId = this.getById(id).getFaultKnowledgeBaseId();
        if (StringUtils.isNotEmpty(faultKnowledgeBaseId)) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseService.getById(faultKnowledgeBaseId);
            if (approvedResult.equals(FaultConstant.PASSED)) {
                faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
                faultKnowledgeBase.setDelFlag(0);
                faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
                faultKnowledgeBase.setApprovedTime(new Date());
                faultKnowledgeBase.setApprovedRemark(approvedRemark);
                faultAnalysisReport.setStatus(FaultConstant.APPROVED);
            } else {
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
                faultKnowledgeBase.setApprovedTime(new Date());
                faultKnowledgeBase.setApprovedRemark(approvedRemark);
                faultAnalysisReport.setStatus(FaultConstant.REJECTED);
            }
            faultKnowledgeBaseService.updateById(faultKnowledgeBase);
        }
        this.updateById(faultAnalysisReport);
        return Result.OK("审批成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(FaultDTO faultDTO) {
        FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
        faultAnalysisReport.setStatus(FaultConstant.PENDING);
        faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);
        FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
        //判断是否同步到知识库
        if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
            faultKnowledgeBase.setFaultCodes(faultDTO.getCode());
            faultKnowledgeBase.setMajorCode(faultDTO.getMajorCode());
            faultKnowledgeBase.setSystemCode(faultDTO.getSubSystemCode());
            //先隐藏，审批通过后再展示
            faultKnowledgeBase.setDelFlag(1);
            faultKnowledgeBaseService.updateById(faultKnowledgeBase);
            faultAnalysisReport.setFaultKnowledgeBaseId(faultKnowledgeBase.getId());
        } else {
            faultAnalysisReport.setFaultKnowledgeBaseId(null);
        }
        this.updateById(faultAnalysisReport);
        return Result.OK("编辑成功!");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addDetail(FaultDTO faultDTO) {
        if (StrUtil.isEmpty(faultDTO.getCode())) {
            return Result.error("故障编号不能为空");
        }
        LambdaQueryWrapper<FaultAnalysisReport> faultAnalysisReportWrapper = new LambdaQueryWrapper<>();
        faultAnalysisReportWrapper.eq(FaultAnalysisReport::getFaultCode, faultDTO.getCode());
        faultAnalysisReportWrapper.eq(FaultAnalysisReport::getDelFlag, 0);
        FaultAnalysisReport analysisReport = this.getBaseMapper().selectOne(faultAnalysisReportWrapper);
        if (ObjectUtil.isNotNull(analysisReport)) {
            return Result.error("已存在该故障的故障分析");
        }
        FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
        faultAnalysisReport.setStatus(FaultConstant.PENDING);
        faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);
        faultAnalysisReport.setDelFlag(0);
        faultAnalysisReport.setScanSum(0);
        FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
        if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
            faultKnowledgeBase.setFaultCodes(faultDTO.getCode());
            faultKnowledgeBase.setMajorCode(faultDTO.getMajorCode());
            faultKnowledgeBase.setSystemCode(faultDTO.getSubSystemCode());
            //先隐藏，审批通过后再展示
            faultKnowledgeBase.setDelFlag(1);
            faultKnowledgeBaseService.save(faultKnowledgeBase);
            faultAnalysisReport.setFaultKnowledgeBaseId(faultKnowledgeBase.getId());
        }
        this.save(faultAnalysisReport);
        return Result.OK("提交成功");
    }

    @Override
    public Result<String> delete(String id) {
        FaultAnalysisReport analysisReport = this.getById(id);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        if (analysisReport.getStatus().equals(FaultConstant.APPROVED)) {
            if (!rolesByUsername.contains(RoleConstant.ADMIN) && !rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
                return Result.error("没有权限");
            }
        }
        this.removeById(id);
        return Result.OK("删除成功!");

    }

    @Override
    public Result<String> deleteBatch(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        for (String s : list) {
            FaultAnalysisReport analysisReport = this.getById(s);
            if (analysisReport.getStatus().equals(FaultConstant.APPROVED)) {
                if (!rolesByUsername.contains(RoleConstant.ADMIN) && !rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
                    return Result.error("没有权限");
                }
            }
        }
        this.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    @Override
    public boolean getRole() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(RoleConstant.ADMIN)&&!rolesByUsername.contains(RoleConstant.FOREMAN)&&!rolesByUsername.contains(RoleConstant.MAJOR_PEOPLE)) {
            return true;
        }
        return false;
    }

    @Override
    public void rejectFirstUserTaskEvent(RejectFirstUserTaskEntity entity) {

    }

    @Override
    public void updateState(UpdateStateEntity updateStateEntity) {
        String businessKey = updateStateEntity.getBusinessKey();
        FaultAnalysisReport analysisReport = this.getById(businessKey);
        if (ObjectUtil.isEmpty(analysisReport)) {
            throw new AiurtBootException("未找到ID为【" + businessKey + "】的数据！");
        }
        int states = updateStateEntity.getStates();
        switch (states) {
            case 0:
                // 技术员审核
                analysisReport.setStatus(FaultConstant.PENDING);
                break;
            case 2:
                // 技术员驳回，更新状态为已驳回状态
                analysisReport.setStatus(FaultConstant.REJECTED);
                analysisReport.setApprovedResult(FaultConstant.NO_PASS);
                break;
            case 3:
                //已审批
                analysisReport.setStatus(FaultConstant.APPROVED);
                analysisReport.setApprovedResult(FaultConstant.PASSED);
                //修改知识库状态
                String faultKnowledgeBaseId = this.getById(analysisReport.getId()).getFaultKnowledgeBaseId();
                if (StringUtils.isNotEmpty(faultKnowledgeBaseId)) {
                    FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseService.getById(faultKnowledgeBaseId);
                    faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
                    faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
                    faultKnowledgeBase.setDelFlag(0);
                    faultKnowledgeBaseService.updateById(faultKnowledgeBase);
                }
                break;
            default:
                break;
        }
        this.updateById(analysisReport);
    }

    @Transactional(rollbackFor = Exception.class)
    public String startProcess(FaultDTO faultDTO){
        String id = faultDTO.getId();
        FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
        faultAnalysisReport.setStatus(FaultConstant.PENDING);
        faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);
        faultAnalysisReport.setDelFlag(0);
        faultAnalysisReport.setScanSum(0);
        FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
        if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
            faultKnowledgeBase.setFaultCodes(faultDTO.getCode());
            faultKnowledgeBase.setMajorCode(faultDTO.getMajorCode());
            faultKnowledgeBase.setSystemCode(faultDTO.getSubSystemCode());
            //先隐藏，审批通过后再展示
            faultKnowledgeBase.setDelFlag(1);
        }
        if (StrUtil.isEmpty(id)) {
            if (StrUtil.isEmpty(faultDTO.getCode())) {
                throw new AiurtBootException("故障编号不能为空");
            }
            if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
                faultKnowledgeBaseService.save(faultKnowledgeBase);
                faultAnalysisReport.setFaultKnowledgeBaseId(faultKnowledgeBase.getId());
            }
            this.save(faultAnalysisReport);
            String newId = faultAnalysisReport.getId();
            return newId;
        }else{
            if (ObjectUtil.isNotNull(faultKnowledgeBase.getId())) {
                faultKnowledgeBaseService.updateById(faultKnowledgeBase);
            } else {
                //如果编辑之后新增同步到知识库则save
                faultKnowledgeBaseService.save(faultKnowledgeBase);
                faultAnalysisReport.setFaultKnowledgeBaseId(faultKnowledgeBase.getId());
            }
            this.updateById(faultAnalysisReport);
            return id;
        }

    }

}
