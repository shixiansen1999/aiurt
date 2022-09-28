package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.aiurt.modules.robot.mapper.TaskFinishInfoMapper;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.service.ITaskFinishInfoService;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfos;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Service
public class TaskFinishInfoServiceImpl extends ServiceImpl<TaskFinishInfoMapper, TaskFinishInfo> implements ITaskFinishInfoService {
    @Autowired
    private TaskFinishService taskFinishService;
    @Autowired
    private IRobotInfoService robotInfoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeRobotTask(Date startTime, Date endTime) {
        if (ObjectUtil.isEmpty(startTime) || ObjectUtil.isEmpty(endTime)) {
            Date now = new Date();
            startTime = DateUtil.offsetHour(now, -1);
            endTime = now;
        }
        String format = "yyyy-MM-dd HH:mm:ss";
        String formatStartTime = DateUtil.format(startTime, format);
        String formatEndTime = DateUtil.format(endTime, format);
        TaskFinishInfos taskFinishInfos = taskFinishService.getTaskFinishInfoByTime(formatStartTime, formatEndTime);
        Optional.ofNullable(taskFinishInfos).orElseGet(TaskFinishInfos::new).getInfos();
        List<com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfo> infos = Optional.ofNullable(taskFinishInfos)
                .orElseGet(TaskFinishInfos::new).getInfos();
        List<TaskFinishInfo> list = new ArrayList<>();
        // 获取所有机器人的id
        Map<String, String> robotIds = robotInfoService.queryIdMappingRobotIp(new ArrayList<>());
        TaskFinishInfo taskFinishInfo = null;
        for (com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfo info : infos) {
            taskFinishInfo = new TaskFinishInfo();
            taskFinishInfo.setTaskId(info.getTaskId())
                    .setTaskName(info.getTaskName())
                    .setTaskType(info.getTaskType())
                    .setTaskPathId(info.getTaskPathId())
                    .setStartTime(StrUtil.isEmpty(info.getStartTime()) ? null : DateUtil.parse(info.getStartTime(), format))
                    .setEndTime(StrUtil.isEmpty(info.getEndTime()) ? null : DateUtil.parse(info.getEndTime(), format))
                    .setFinishState(info.getFinishState())
                    .setRobotId(robotIds.get(info.getExcuteRobot()));
            list.add(taskFinishInfo);
        }
        // 删除系统上的任务模板数据
        LambdaQueryWrapper<TaskFinishInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(TaskFinishInfo::getTaskPathId, list.stream().map(TaskFinishInfo::getTaskPathId).collect(Collectors.toList()));
        baseMapper.delete(wrapper);
        // 批量更新任务模板信息
        saveOrUpdateBatch(list);
    }

}
