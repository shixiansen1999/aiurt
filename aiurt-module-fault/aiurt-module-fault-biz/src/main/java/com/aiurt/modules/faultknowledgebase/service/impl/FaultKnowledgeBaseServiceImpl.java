package com.aiurt.modules.faultknowledgebase.service.impl;

import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultanalysisreport.entity.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
@Service
public class FaultKnowledgeBaseServiceImpl extends ServiceImpl<FaultKnowledgeBaseMapper, FaultKnowledgeBase> implements IFaultKnowledgeBaseService {

    @Autowired
    private FaultKnowledgeBaseMapper faultKnowledgeBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;

    @Override
    public IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        //根据用户角色是否显示未通过的知识库
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.Maintenance_Worker)&&!rolesByUsername.contains(FaultConstant.Professional_Technical_Director)) {
            faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
        }
        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll(page, faultKnowledgeBase,allSubSystem);
        faultKnowledgeBases.forEach(f->{
            String faultCodes = f.getFaultCodes();
            String[] split = faultCodes.split(",");
            List<String> list = Arrays.asList(split);
            f.setFaultCodeList(list);
        });
        //正序
        String asc = "asc";
        if (asc.equals(faultKnowledgeBase.getOrder())) {
            List<FaultKnowledgeBase> reportList = faultKnowledgeBases.stream().sorted(Comparator.comparing(FaultKnowledgeBase::getCreateTime)).collect(Collectors.toList());
            return page.setRecords(reportList);
        }

        return page.setRecords(faultKnowledgeBases);
    }

    @Override
    public IPage<FaultDTO> getFault(Page<FaultDTO> page, FaultDTO faultDTO) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        List<String> allSubSystem = faultKnowledgeBaseTypeMapper.getAllSubSystem(sysUser.getId());
        List<FaultDTO> faults = faultAnalysisReportMapper.getFault(page, faultDTO,allSubSystem,null);
        return page.setRecords(faults);
    }

    @Override
    public Result<String> approval(String approvedRemark, Integer approvedResult, String id) {
        if ( getRole()) {return Result.OK("没有权限");}
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        FaultKnowledgeBase faultKnowledgeBase = new FaultKnowledgeBase();
        faultKnowledgeBase.setId(id);
        faultKnowledgeBase.setApprovedRemark(approvedRemark);
        faultKnowledgeBase.setApprovedResult(approvedResult);
        faultKnowledgeBase.setApprovedTime(new Date());
        faultKnowledgeBase.setApprovedUserName(sysUser.getUsername());
        if (approvedResult.equals(FaultConstant.NO_PASS)) {
            faultKnowledgeBase.setStatus(FaultConstant.REJECTED);
        } else {
            faultKnowledgeBase.setStatus(FaultConstant.APPROVED);
        }
        this.updateById(faultKnowledgeBase);
        return Result.OK("审批成功!");

    }

    public boolean getRole() {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> rolesByUsername = sysBaseAPI.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.Maintenance_Worker)&&!rolesByUsername.contains(FaultConstant.Professional_Technical_Director)) {
            return true;
        }
        return false;
    }
}
