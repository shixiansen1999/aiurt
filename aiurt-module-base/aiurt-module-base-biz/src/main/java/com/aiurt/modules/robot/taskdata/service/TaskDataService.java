package com.aiurt.modules.robot.taskdata.service;


import com.aiurt.modules.robot.taskdata.wsdl.*;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
public interface TaskDataService {

    /**
     * 机器人任务模板信息
     * @return
     */
    TaskPathInfos getTaskPathInfo();

    /**
     * 根据任务模板id给机器人发任务
     *
     * @param taskPathId 任务模板id
     * @return TaskInfo
     */
    TaskInfo startTaskByPathId(String taskPathId);

    /**
     * 机器人任务操作
     *
     * @param type CancelTask 取消机器人当前任务
     *             PauseTask 暂停机器人当前任务
     *             ResumeTask 恢复机器人当前任务
     *             ChargeTask 机器人返回充电
     * @return
     */
    int robotControlTask(ControlTaskType type);

    /** 获取任务执行信息
     * @param robotIp 机器人ip
     * @return TaskExcuteData
     */
    TaskExcuteData getTaskExcuteData(String robotIp);

    /**
     * 机器人巡检区域信息
     * @return
     */
    PatrolAreaInfos getPatrolAreaInfo();

    /**
     * 机器人巡检点位信息
     * @return
     */
    PatrolPointInfos getPatrolPointInfo();

}
