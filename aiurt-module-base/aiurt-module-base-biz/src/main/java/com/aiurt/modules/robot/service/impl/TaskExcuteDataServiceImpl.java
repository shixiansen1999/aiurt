package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.entity.TaskExcuteData;
import com.aiurt.modules.robot.mapper.TaskExcuteDataMapper;
import com.aiurt.modules.robot.service.IRobotInfoService;
import com.aiurt.modules.robot.service.ITaskExcuteDataService;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: task_excute_data
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Service
public class TaskExcuteDataServiceImpl extends ServiceImpl<TaskExcuteDataMapper, TaskExcuteData> implements ITaskExcuteDataService {
    @Resource
    private TaskDataService taskDataService;
    @Autowired
    private IRobotInfoService robotInfoService;

    /**
     * 当前机器人执行的任务
     *
     * @param robotIp 机器人ip
     * @return
     */
    @Override
    public TaskExcuteData getTaskExcuteData(String robotIp) {
        if (StrUtil.isEmpty(robotIp)) {
            return new TaskExcuteData();
        }

        com.aiurt.modules.robot.taskdata.wsdl.TaskExcuteData taskExcuteData = taskDataService.getTaskExcuteData(robotIp);
        return null;
    }

    /**
     * 同步机器人当前执行任务信息
     *
     * @param robotIdList 机器人ip集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeTaskExcuteData(List<String> robotIdList) {

        // 查询机器人id对应的机器人ip映射关系
        Map<String, String> mappingRobotIp = robotInfoService.queryIdMappingRobotIp(robotIdList);

        // 记录原来任务的状态信息


        // 删除原来的执行任务数据
        LambdaQueryWrapper<TaskExcuteData> lam = new LambdaQueryWrapper<>();
        if (CollUtil.isNotEmpty(robotIdList)) {
            lam.in(TaskExcuteData::getRobotId, robotIdList);
        }
        baseMapper.delete(lam);


        // 封装数据，批量更新
        Set<TaskExcuteData> result = CollUtil.newHashSet();
        TaskExcuteData taskExcuteData = null;
        for (Map.Entry<String, String> entry : mappingRobotIp.entrySet()) {
            com.aiurt.modules.robot.taskdata.wsdl.TaskExcuteData excuteData = taskDataService.getTaskExcuteData(entry.getValue());
            taskExcuteData = TaskExcuteData
                    .builder()
                    .errorDeviceSize(excuteData.getErrorDeviceSize())
                    .finishDeviceSize(excuteData.getFinishDeviceSize())
                    .patrolDeviceId(excuteData.getPatrolDeviceId())
                    .patrolDeviceName(excuteData.getPatrolDeviceName())
                    .robotId(entry.getKey())
                    .taskFinishPercentage(excuteData.getTaskFinishPercentage())
                    .taskId(excuteData.getTaskId())
                    .taskName(excuteData.getTaskName())
                    .taskType(excuteData.getTaskType())
                    .totalDeviceSize(excuteData.getTotalDeviceSize())
                    .taskStatus(null)
                    .build();
            result.add(taskExcuteData);
        }

        // 再次检查需要更新的当前执行任务信息是否为空
        if (CollUtil.isEmpty(result)) {
            return;
        }

        // 批量同步远程机器人当前执行任务数据
        saveOrUpdateBatch(result);
    }

}
