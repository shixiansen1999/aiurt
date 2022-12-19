package com.aiurt.modules.faultknowledgebase.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.faultanalysisreport.constant.FaultConstant;
import com.aiurt.modules.faultanalysisreport.dto.FaultDTO;
import com.aiurt.modules.faultanalysisreport.mapper.FaultAnalysisReportMapper;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.mapper.FaultKnowledgeBaseMapper;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.mapper.FaultKnowledgeBaseTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private FaultKnowledgeBaseTypeMapper faultKnowledgeBaseTypeMapper;
    @Autowired
    private FaultAnalysisReportMapper faultAnalysisReportMapper;

    @Override
    public IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //当前用户拥有的子系统
        LambdaQueryWrapper<FaultKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
        List<FaultKnowledgeBase> bases = faultKnowledgeBaseMapper.selectList(queryWrapper.eq(FaultKnowledgeBase::getDelFlag, "0"));
        List<String> ids = bases.stream().map(FaultKnowledgeBase::getId).distinct().collect(Collectors.toList());
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        //根据用户角色是否显示未通过的知识库
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)&&!rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
            faultKnowledgeBase.setApprovedResult(FaultConstant.PASSED);
        }
        //工班长只能看到审核通过的和自己创建的未审核通过的
        if (rolesByUsername.size()==1 && rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)) {
            faultKnowledgeBase.setCreateBy(sysUser.getUsername());
        }
        //下面禁用数据过滤
        boolean b = GlobalThreadLocal.setDataFilter(false);
        String id = faultKnowledgeBase.getId();
        //根据id条件查询时，jeecg前端会传一个id结尾带逗号的id，所以先去掉结尾id
        if (StringUtils.isNotBlank(id)) {
            String substring = id.substring(0, id.length() - 1);
            faultKnowledgeBase.setId(substring);
        }

        List<FaultKnowledgeBase> faultKnowledgeBases = faultKnowledgeBaseMapper.readAll(page, faultKnowledgeBase,ids);
        GlobalThreadLocal.setDataFilter(b);
        faultKnowledgeBases.forEach(f->{
            String faultCodes = f.getFaultCodes();
            if (StrUtil.isNotBlank(faultCodes)) {
                String[] split = faultCodes.split(",");
                List<String> list = Arrays.asList(split);
                f.setFaultCodeList(list);
            }
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
        if ( getRole()) {return Result.error("没有权限");}
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
        List<String> rolesByUsername = sysBaseApi.getRolesByUsername(sysUser.getUsername());
        if (!rolesByUsername.contains(FaultConstant.ADMIN)&&!rolesByUsername.contains(FaultConstant.MAINTENANCE_WORKER)&&!rolesByUsername.contains(FaultConstant.PROFESSIONAL_TECHNICAL_DIRECTOR)) {
            return true;
        }
        return false;
    }
}
