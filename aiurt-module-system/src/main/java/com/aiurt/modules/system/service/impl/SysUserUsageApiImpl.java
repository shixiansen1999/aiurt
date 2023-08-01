package com.aiurt.modules.system.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.system.entity.SysUserUsage;
import com.aiurt.modules.system.service.ISysUserUsageService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysUserUsageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: fgw
 * @Date:2023-7-31
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysUserUsageApiImpl implements ISysUserUsageApi {

    @Autowired
    private ISysUserUsageService sysUserUsageService;

    /**
     * 更新用户使用数
     *
     * @param userNameList 被使用的userName
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSysUserUsage(String userId, List<String> userNameList) {

        // 查询现有的数据
        Set<String> userNameSet = sysUserUsageService.queryUserNameSetByUserId(userId);

        // 获取交集，修改数据
        List<String> intersection = new ArrayList<>(userNameList);
        intersection.retainAll(userNameSet);

        // 新增数据
        List<String> difference = new ArrayList<>(userNameList);
        difference.removeAll(userNameSet);

        if (CollUtil.isNotEmpty(intersection)) {
            LambdaUpdateWrapper<SysUserUsage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUserUsage::getUserId, userId).in(SysUserUsage::getPersonnelUserName, intersection)
                    .setSql("`usage_count` = `usage_count` + 1");

            sysUserUsageService.update(updateWrapper);
        }


        List<SysUserUsage> usageList = difference.stream().map(userName -> {
            SysUserUsage usage = new SysUserUsage();
            usage.setDelFlag(0);
            usage.setUserId(userId);
            usage.setUsageCount(1);
            usage.setPersonnelUserName(userName);
            return usage;
        }).collect(Collectors.toList());

        if (CollUtil.isNotEmpty(usageList)) {
            sysUserUsageService.saveBatch(usageList);
        }
    }
}
