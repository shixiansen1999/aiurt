package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
public interface ITaskFinishInfoService extends IService<TaskFinishInfo> {

    /**
     * 刷新同步巡检任务数据
     *
     * @param startTime
     * @param endTime
     */
    void synchronizeRobotTask(Date startTime, Date endTime);
}
