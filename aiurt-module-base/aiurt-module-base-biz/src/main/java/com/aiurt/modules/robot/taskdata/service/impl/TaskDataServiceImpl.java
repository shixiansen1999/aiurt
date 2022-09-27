package com.aiurt.modules.robot.taskdata.service.impl;

import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.taskdata.service.TaskDataService;
import com.aiurt.modules.robot.taskdata.wsdl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
@Service
public class TaskDataServiceImpl implements TaskDataService {
    @Value("${robot.wsdl.address}")
    private String address;


    /**
     * 机器人任务模板信息
     */
    @Override
    public TaskPathInfos getTaskPathInfo() {
        TaskPathInfos taskPathInfo = WebServiceUtils.getWebService(ServicePortType.class, address).getTaskPathInfo();
        return taskPathInfo;
    }

    /**
     * 根据任务模板id给机器人发任务
     *
     * @param taskPathId 任务模板id
     * @return TaskInfo
     */
    @Override
    public TaskInfo startTaskByPathId(String taskPathId) {
        TaskInfo taskInfo = WebServiceUtils.getWebService(ServicePortType.class, address).startTaskByPathId(taskPathId);
        return taskInfo;
    }

    /**
     * 机器人任务操作
     *
     * @param type CancelTask 取消机器人当前任务
     *             PauseTask 暂停机器人当前任务
     *             ResumeTask 恢复机器人当前任务
     *             ChargeTask 机器人返回充电
     * @return 0成功1失败
     */
    @Override
    public int robotControlTask(ControlTaskType type) {
        int result = WebServiceUtils.getWebService(ServicePortType.class, address).robotControlTask(type);
        return result;
    }

    /**
     * 获取任务执行信息
     *
     * @param robotIp 机器人ip
     * @return TaskExcuteData
     */
    @Override
    public TaskExcuteData getTaskExcuteData(String robotIp) {
        TaskExcuteData info = WebServiceUtils.getWebService(ServicePortType.class, address).getTaskExcuteData(robotIp);
        return info;
    }

    /**
     * 机器人巡检区域信息
     *
     * @return
     */
    @Override
    public PatrolAreaInfos getPatrolAreaInfo() {
        return WebServiceUtils.getWebService(ServicePortType.class, address).getPatrolAreaInfo();
    }

    /**
     * 机器人巡检点位信息
     *
     * @return
     */
    @Override
    public PatrolPointInfos getPatrolPointInfo() {
        return WebServiceUtils.getWebService(ServicePortType.class, address).getPatrolPointInfo();
    }
}
