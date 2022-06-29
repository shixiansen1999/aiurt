package com.aiurt.modules.faultanalysisreport.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.entity.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultanalysisreport.service.IFaultAnalysisReportService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebasetype.dto.SubSystemDTO;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public IPage<FaultAnalysisReport> readAll(Page<FaultAnalysisReport> page, FaultAnalysisReport faultAnalysisReport) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(FaultConstant.ADMIN)) {
            faultAnalysisReport.setApprovedResult(1);
        }
        //根据角色决定是否查询未审核通过的故障分析
        List<FaultAnalysisReport> faultAnalysisReports = faultAnalysisReportMapper.readAll(page, faultAnalysisReport,allSubSystem);
        return page.setRecords(faultAnalysisReports);
    }

    @Override
    public FaultDTO readOne(String id) {
        FaultAnalysisReport faultAnalysisReport = faultAnalysisReportMapper.readOne(id);
        FaultDTO faultDTO = new FaultDTO();
        faultDTO.setFaultAnalysisReport(faultAnalysisReport);
        if (StringUtils.isNotEmpty(faultAnalysisReport.getFaultKnowledgeBaseId())) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.selectById(faultAnalysisReport.getFaultKnowledgeBaseId());
            faultDTO.setFaultKnowledgeBase(faultKnowledgeBase);
        }
        return faultDTO;
    }

    @Override
    public IPage<Fault> getFault(Page<Fault> page, Fault fault) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        List<Fault> faults = faultAnalysisReportMapper.getFault(page, fault,allSubSystem);
        return page.setRecords(faults);
    }

    @Override
    public FaultDTO getDetail(String id) {
        FaultDTO faultDTO = faultAnalysisReportMapper.getDetail(id);
        LambdaQueryWrapper<FaultAnalysisReport> reportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportLambdaQueryWrapper.eq(FaultAnalysisReport::getFaultCode, faultDTO.getCode());
        FaultAnalysisReport faultAnalysisReport = faultAnalysisReportMapper.selectOne(reportLambdaQueryWrapper);
        faultDTO.setFaultAnalysisReport(faultAnalysisReport);
        if (StringUtils.isNotEmpty(faultAnalysisReport.getFaultKnowledgeBaseId())) {
            FaultKnowledgeBase faultKnowledgeBase = faultKnowledgeBaseMapper.selectById(faultAnalysisReport.getFaultKnowledgeBaseId());
            faultDTO.setFaultKnowledgeBase(faultKnowledgeBase);
        }
        return faultDTO;
    }


}
