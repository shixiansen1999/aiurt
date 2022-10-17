package com.aiurt.modules.subsystem.service.impl;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.aiurt.modules.subsystem.entity.CsSubsystemUser;
import com.aiurt.modules.subsystem.mapper.CsSubsystemMapper;
import com.aiurt.modules.subsystem.mapper.CsSubsystemUserMapper;
import com.aiurt.modules.subsystem.service.ICsSubsystemService;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.mapper.CsUserSubsystemMapper;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liquibase.pro.packaged.L;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class CsSubsystemServiceImpl extends ServiceImpl<CsSubsystemMapper, CsSubsystem> implements ICsSubsystemService {
    @Autowired
    private CsSubsystemMapper csSubsystemMapper;
    @Autowired
    private CsSubsystemUserMapper csSubsystemUserMapper;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private CsUserSubsystemMapper csUserSubsystemMapper;
    /**
     * 添加
     *
     * @param csSubsystem
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> add(CsSubsystem csSubsystem) {
        //子系统编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsSubsystem::getSystemCode, csSubsystem.getSystemCode());
        queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> list = csSubsystemMapper.selectList(queryWrapper);
        if (!list.isEmpty()) {
            return Result.error("子系统编码重复，请重新填写！");
        }
        //子系统名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsSubsystem::getMajorCode, csSubsystem.getMajorCode());
        nameWrapper.eq(CsSubsystem::getSystemName, csSubsystem.getSystemName());
        nameWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> nameList = csSubsystemMapper.selectList(nameWrapper);
        if (!nameList.isEmpty()) {
            return Result.error("子系统名称重复，请重新填写！");
        }
        csSubsystemMapper.insert(csSubsystem);
        //插入子系统人员表
        insertSystemUser(csSubsystem);
        return Result.OK("添加成功！");
    }
    /**
     * 修改
     *
     * @param csSubsystem
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> update(CsSubsystem csSubsystem) {
        //删除原子系统人员表
        QueryWrapper<CsSubsystemUser> userQueryWrapper = new QueryWrapper<CsSubsystemUser>();
        userQueryWrapper.eq("subsystem_id", csSubsystem.getId());
        csSubsystemUserMapper.delete(userQueryWrapper);
        //子系统编码不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsSubsystem::getSystemCode, csSubsystem.getSystemCode());
        queryWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> list = csSubsystemMapper.selectList(queryWrapper);
        if (!list.isEmpty() && !list.get(0).getId().equals(csSubsystem.getId())) {
            return Result.error("子系统编码重复，请重新填写！");
        }
        //子系统名称不能重复，判断数据库中是否存在，如不存在则可继续添加
        LambdaQueryWrapper<CsSubsystem> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(CsSubsystem::getMajorCode, csSubsystem.getMajorCode());
        nameWrapper.eq(CsSubsystem::getSystemName, csSubsystem.getSystemName());
        nameWrapper.eq(CsSubsystem::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<CsSubsystem> nameList = csSubsystemMapper.selectList(nameWrapper);
        if (!nameList.isEmpty() && !nameList.get(0).getId().equals(csSubsystem.getId())) {
            return Result.error("子系统名称重复，请重新填写！");
        }
        csSubsystemMapper.updateById(csSubsystem);
        //插入子系统人员表
        insertSystemUser(csSubsystem);
        return Result.OK("编辑成功！");
    }

    @Override
    public Page<SubsystemFaultDTO> getSubsystemFailureReport(Page<?> page, String time, String subsystemCode, List<String> deviceTypeCode) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> subSystemCodes = new ArrayList<>();
        if (StringUtils.isNotEmpty(subsystemCode)){
            subSystemCodes.add(subsystemCode);
        }else {
            subSystemCodes = csUserSubsystemMapper.selectByUserId(sysUser.getId());
        }
        List<SubsystemFaultDTO> subsystemFaultDTOS = new ArrayList<>();
        subSystemCodes.forEach(s -> {
            SubsystemFaultDTO subDTO = csUserSubsystemMapper.getSubsystemFaultDTO(time,s);
            subDTO.setFailureDuration(new BigDecimal((1.0 * ( subDTO.getNum()) / 3600)).setScale(2, BigDecimal.ROUND_HALF_UP));
            List<String> list = csUserSubsystemMapper.getSubsystemByDeviceType(time,s,deviceTypeCode);

        });
        return null;
    }

    public void insertSystemUser(CsSubsystem csSubsystem){
        if(null!=csSubsystem.getSystemUserList()){
            String[] arr = csSubsystem.getSystemUserList().split(",");
            for(String userName :arr){
                CsSubsystemUser systemUser = new CsSubsystemUser();
                systemUser.setSubsystemId(csSubsystem.getId()+"");
                systemUser.setUsername(userName);
                LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysUser::getUsername, userName);
                List<SysUser> userList = sysUserService.list(queryWrapper);
                if(!userList.isEmpty()){
                    systemUser.setUserId(userList.get(0).getId());
                }
                csSubsystemUserMapper.insert(systemUser);
            }
        }
    }
}
