package com.aiurt.modules.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.mapper.ActCustomUserMapper;
import com.aiurt.modules.user.service.IActCustomUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.*;
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
        ActCustomUser actCustomUser = getActCustomUserByTaskInfo(processDefinitionId,taskId,type);

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

    @Override
    public ActCustomUser getActCustomUserByTaskInfo(String processDefinitionId, String taskId, String type) {
        LambdaQueryWrapper<ActCustomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomUser::getProcessDefinitionId, processDefinitionId)
                .eq(ActCustomUser::getTaskId, taskId)
                .eq(ActCustomUser::getType, type)
                .eq(ActCustomUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询人员
     *
     * @param processDefinitionId
     * @param nodeId
     * @param type
     * @return
     */
    @Override
    public List<String> getUserByTaskInfo(String processDefinitionId, String nodeId, String type) {
        LambdaQueryWrapper<ActCustomUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomUser::getProcessDefinitionId, processDefinitionId)
                .eq(ActCustomUser::getTaskId, nodeId)
                .eq(ActCustomUser::getType, type)
                .eq(ActCustomUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        ActCustomUser customUser = baseMapper.selectOne(queryWrapper);
        if (Objects.isNull(customUser)) {
            return Collections.emptyList();
        }

        String userName = customUser.getUserName();
        String post = customUser.getPost();
        String orgId = customUser.getOrgId();
        String roleCode = customUser.getRoleCode();

        List<String> resultList = new ArrayList<>();
        List<String> list = StrUtil.split(userName, ',');
        List<String>  userNameList = sysBaseApi.getUserNameByParams(StrUtil.split(roleCode, ','), StrUtil.split(orgId, ','), StrUtil.split(post, ','));
        resultList.addAll(list);
        resultList.addAll(userNameList);
        resultList = resultList.stream().distinct().collect(Collectors.toList());
        return resultList;
    }
}
