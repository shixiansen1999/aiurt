package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.entity.TaskExcuteData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: task_excute_data
 * @Author: aiurt
 * @Date:   2022-09-28
 * @Version: V1.0
 */
public interface ITaskExcuteDataService extends IService<TaskExcuteData> {

    /**
     * 当前机器人执行的任务
     * @param robotIp 机器人ip
     * @return
     */
    TaskExcuteData getTaskExcuteData(String robotIp);

    /**
     * 同步机器人当前执行任务信息
     * @param robotIdList 机器人id集合
     */
    void synchronizeTaskExcuteData(List<String> robotIdList);
}
