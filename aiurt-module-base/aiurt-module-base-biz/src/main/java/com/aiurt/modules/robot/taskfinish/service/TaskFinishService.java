package com.aiurt.modules.robot.taskfinish.service;


import com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfos;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfos;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
public interface TaskFinishService {

    /**
     * 根据时间获取机器人任务列表信息
     *
     * @param startTime 开始时间,格式2018-12-14 09:36:23
     * @param endTime   结束时间,格式2018-12-15 09:36:23
     * @return TaskFinishInfos
     */
    TaskFinishInfos getTaskFinishInfoByTime(String startTime, String endTime);

    /**
     * 根据任务id获取机器人完成任务数据信息
     * @param taskId 任务id
     * @return TaskExcuteInfos
     */
    TaskExcuteInfos getTaskExcuteInfoByTaskId(String taskId);
}
