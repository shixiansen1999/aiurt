package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.robot.entity.TaskExcuteData;
import com.aiurt.modules.robot.mapper.TaskExcuteDataMapper;
import com.aiurt.modules.robot.service.ITaskExcuteDataService;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

        // 删除原来的执行任务数据
        LambdaQueryWrapper<TaskExcuteData> lam = new LambdaQueryWrapper<>();
        if(CollUtil.isNotEmpty(robotIdList)){
            lam.in(TaskExcuteData::getRobotId);
        }
        baseMapper.delete(lam);

        // 同步远程数据
        for (String robotId : robotIdList) {

        }
        com.aiurt.modules.robot.taskdata.wsdl.TaskExcuteData taskExcuteData = taskDataService.getTaskExcuteData(null);


    }

}
