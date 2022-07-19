package com.aiurt.modules.faultanalysisreport.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.entity.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
public class FaultAnalysisReportServiceImpl extends ServiceImpl<FaultAnalysisReportMapper, FaultAnalysisReport> implements IFaultAnalysisReportService {

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

    @Override
    public IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        //根据角色决定是否查询未审核通过的故障分析
        if ( getRole()) {faultAnalysisReport.setApprovedResult(FaultConstant.PASSED);}
        //工班长只能看到审核通过的和自己创建的未审核通过的
        if (allSubSystem.size()==1 && allSubSystem.contains(FaultConstant.MAINTENANCE_WORKER)) {
            faultAnalysisReport.setCreateBy(sysUser.getUsername());
        }
        List<FaultAnalysisReport> faultAnalysisReports = faultAnalysisReportMapper.readAll(page, faultAnalysisReport,allSubSystem);
        String asc = "asc";
        if (asc.equals(faultAnalysisReport.getOrder())) {
            List<FaultAnalysisReport> reportList = faultAnalysisReports.stream().sorted(Comparator.comparing(FaultAnalysisReport::getCreateTime)).collect(Collectors.toList());
            return page.setRecords(reportList);
        }
        return page.setRecords(faultAnalysisReports);
    }

    @Override
    public FaultAnalysisReport readOne(String id,String faultCode) {
        return faultAnalysisReportMapper.readOne(id,faultCode);
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        //查询已经被引用的故障
        List<String> faultCodes = faultAnalysisReportMapper.getFaultCode();
        List<FaultDTO> faults = faultAnalysisReportMapper.getFault(page, faultDTO,allSubSystem,faultCodes);
        return page.setRecords(faults);
    }

    @Override
    public IPage<FaultDTO> getDetail(String id) {
        //获取故障详情
        FaultDTO faultDTO = faultAnalysisReportMapper.getDetail(id);
        //获取故障分析详情
        LambdaQueryWrapper<FaultAnalysisReport> reportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLambdaQueryWrapper.eq(FaultAnalysisReport::getFaultCode, faultDTO.getCode());
        FaultAnalysisReport faultAnalysisReport = faultAnalysisReportMapper.selectOne(reportLambdaQueryWrapper);
        faultDTO.setFaultAnalysisReport(faultAnalysisReport);
        //获取故障知识详情
        if (ObjectUtils.isNotEmpty(faultAnalysisReport) && StringUtils.isNotEmpty(faultAnalysisReport.getFaultKnowledgeBaseId())) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.selectById(faultAnalysisReport.getFaultKnowledgeBaseId());
            faultDTO.setFaultKnowledgeBase(faultKnowledgeBase);
        }
        return page().setRecords(faultDTO);
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if ( getRole()) {return Result.OK("没有权限");}
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
                faultAnalysisReport.setDelFlag(0);
                faultAnalysisReport.setStatus(FaultConstant.APPROVED);
            } else {
                faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
                faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
                faultAnalysisReport.setStatus(FaultConstant.REJECTED);
            }
            faultKnowledgeBaseService.updateById(faultKnowledgeBase);
        }
        this.updateById(faultAnalysisReport);
        return Result.OK("审批成功!");
    }

    @Override
    public Result<String> edit(FaultDTO faultDTO) {
        FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
        faultAnalysisReport.setStatus(FaultConstant.PENDING);
        faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);

        FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
        //判断是否同步到知识库
        if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
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
    public Result<String> addDetail(FaultDTO faultDTO) {
        FaultAnalysisReport faultAnalysisReport = faultDTO.getFaultAnalysisReport();
        faultAnalysisReport.setStatus(FaultConstant.PENDING);
        faultAnalysisReport.setApprovedResult(FaultConstant.NO_PASS);
        faultAnalysisReport.setDelFlag(0);
        faultAnalysisReport.setScanSum(0);
        FaultKnowledgeBase faultKnowledgeBase = faultDTO.getFaultKnowledgeBase();
        if (ObjectUtil.isNotNull(faultKnowledgeBase)) {
            faultKnowledgeBase.setStatus(FaultConstant.PENDING);
            faultKnowledgeBase.setApprovedResult(FaultConstant.NO_PASS);
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
            if (!rolesByUsername.contains(FaultConstant.ADMIN) && !rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
                return Result.OK("没有权限");
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
                if (!rolesByUsername.contains(FaultConstant.ADMIN) && !rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
                    return Result.OK("没有权限");
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
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)&&!rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
            return true;
        }
        return false;
    }

}
