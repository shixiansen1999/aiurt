package com.aiurt.modules.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.system.dto.SysUserUsageRespDTO;
import com.aiurt.modules.system.entity.SysUserUsage;
import com.aiurt.modules.system.mapper.SysUserUsageMapper;
import com.aiurt.modules.system.service.ISysUserUsageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 系统用户被选用频率表
 * @Author: aiurt
 * @Date:   2023-07-24
 * @Version: V1.0
 */
@Service
public class SysUserUsageServiceImpl extends ServiceImpl<SysUserUsageMapper, SysUserUsage> implements ISysUserUsageService {

    /**
     * 查询常用的用户信息
     *
     * @return
     */
    @Override
    public List<SysUserUsageRespDTO> queryList(String search) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        List<SysUserUsageRespDTO> list = baseMapper.queryList(loginUser.getId(), search);
        return list;
    }

    /**
     * 全局搜索
     *
     * @param name
     * @return
     */
    @Override
    public List<SysUserUsageRespDTO> globalSearch(String name) {
        if (StrUtil.isEmpty(name)) {
            return Collections.emptyList();
        }
        List<SysUserUsageRespDTO> list = baseMapper.globalSearch(name);

        return list;
    }

    /**
     * 查询当前用户的数据
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> queryUserNameSetByUserId(String userId) {
        LambdaQueryWrapper<SysUserUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserUsage::getUserId, userId);

        List<SysUserUsage> list = this.list(wrapper);

        Set<String> set = list.stream().map(SysUserUsage::getPersonnelUserName).collect(Collectors.toSet());
        return set;
    }
}
