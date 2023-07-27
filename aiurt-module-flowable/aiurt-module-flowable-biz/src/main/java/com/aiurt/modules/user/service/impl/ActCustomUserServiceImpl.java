package com.aiurt.modules.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.mapper.ActCustomUserMapper;
import com.aiurt.modules.user.service.IActCustomUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 流程办理人与抄送人
 * @Author: aiurt
 * @Date: 2023-07-25
 * @Version: V1.0
 */
@Service
public class ActCustomUserServiceImpl extends ServiceImpl<ActCustomUserMapper, ActCustomUser> implements IActCustomUserService {

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public List<String> getUserNamesByProcessAndTask(String processDefinitionId, String taskId, String type) {
        LambdaQueryWrapper<ActCustomUser> lam = new LambdaQueryWrapper<>();
        lam.eq(ActCustomUser::getProcessDefinitionId, processDefinitionId);
        lam.eq(ActCustomUser::getTaskId, taskId);
        lam.eq(ActCustomUser::getType, type);
        lam.eq(ActCustomUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        ActCustomUser actCustomUser = baseMapper.selectOne(lam);

        if (ObjectUtil.isEmpty(actCustomUser)) {
            return CollUtil.newArrayList();
        }

        List<String> userNames = StrUtil.split(actCustomUser.getUserName(), ',');
        List<String> roleCodes = StrUtil.split(actCustomUser.getRoleCode(), ',');
        List<String> orgIds = StrUtil.split(actCustomUser.getOrgId(), ',');
        List<String> posts = StrUtil.split(actCustomUser.getPost(), ',');

        if (CollUtil.isEmpty(userNames) && CollUtil.isEmpty(roleCodes) && CollUtil.isEmpty(orgIds) && CollUtil.isEmpty(posts)) {
            return CollUtil.newArrayList();
        }

        List<String> userNameList = sysBaseApi.getUserNameByParams(roleCodes, orgIds, posts);

        // 并集、去重
        List<String> mergedUserNames = Stream.of(
                        Optional.ofNullable(userNames).orElse(Collections.emptyList()).stream(),
                        Optional.ofNullable(userNameList).orElse(Collections.emptyList()).stream())
                .flatMap(userNameStr -> userNameStr)
                .distinct()
                .collect(Collectors.toList());

        return mergedUserNames;
    }
}
