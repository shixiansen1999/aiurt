package com.aiurt.modules.robot.taskdata;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.taskdata.wsdl.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1916:07
 */
@Slf4j
public class TaskDataClientTest {
    /**
     * WebService服务地址
     */
    private static final String ADDRESS = "http://2.0.1.16:11456?wsdl";
    /**
     * 机器人ip
     */
    private static final String ROBOT_IP = "192.168.1.10";


    /**
     * 机器人任务模板信息
     */
    public void getTaskPathInfo() {
        TaskPathInfos taskPathInfo = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getTaskPathInfo();
        if (ObjectUtil.isNotEmpty(taskPathInfo)) {
            List<TaskPathInfo> infos = taskPathInfo.getInfos();
            for (TaskPathInfo info : infos) {
                System.out.print("任务模板id：" + info.getTaskPathId());
                System.out.print(" | 任务模板名称：" + info.getTaskPathName());
                System.out.print(" | 巡检点位列表：" + info.getPointList());
                System.out.print(" | 任务模板类型：" + info.getTaskPathType());
                System.out.print(" | 创建时间：" + info.getCreateTime());
                System.out.print(" | 完成动作：" + info.getFinishAction());
                System.out.println();
            }
        }
    }

    /**
     * 根据任务模板id给机器人发任务
     */
    public void startTaskByPathId() {
        // 任务模板id
        String taskPathId = "0c53b6942b728218301d0aaef2384fd5";
        TaskInfo taskInfo = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).startTaskByPathId(taskPathId);
        if (ObjectUtil.isNotEmpty(taskInfo)) {
            System.out.print("操作是否成功0成功，1失败：" + taskInfo.getResult());
            System.out.print(" | 任务id：" + taskInfo.getTaskId());
            System.out.println();
        }
    }

    /**
     * 机器人任务操作
     * CancelTask 取消机器人当前任务
     * PauseTask 暂停机器人当前任务
     * ResumeTask 恢复机器人当前任务
     * ChargeTask 机器人返回充电
     */
    public void robotControlTask() {
        int result = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).robotControlTask(ControlTaskType.PAUSE_TASK);
        System.out.println(result > 0 ? "失败" : "成功");
    }

    /**
     * 获取任务执行信息
     * CancelTask 取消机器人当前任务
     * PauseTask 暂停机器人当前任务
     * ResumeTask 恢复机器人当前任务
     * ChargeTask 机器人返回充电
     */
    public void getTaskExcuteData() {
        TaskExcuteData info = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getTaskExcuteData(ROBOT_IP);
        if (ObjectUtil.isNotEmpty(info)) {
            System.out.print("异常数量：" + info.getErrorDeviceSize());
            System.out.print(" | 已完成数量：" + info.getFinishDeviceSize());
            System.out.print(" | 当前巡检点Id：" + info.getPatrolDeviceId());
            System.out.print(" | 当前巡检点名称：" + info.getPatrolDeviceName());
            System.out.print(" | 机器人Ip：" + info.getRobotIp());
            System.out.print(" | 完成进度：" + info.getTaskFinishPercentage());
            System.out.print(" | 任务Id：" + info.getTaskId());
            System.out.print(" | 任务名称：" + info.getTaskName());
            System.out.print(" | 任务类型：" + info.getTaskType());
            System.out.println();
        }
    }
}

