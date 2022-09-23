package com.aiurt.modules.robot.taskfinish;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.taskfinish.wsdl.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;


/**
 * 
 *
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1916:08
 */
@Slf4j
public class TaskFinishClientTest {
    /**
     * WebService服务地址
     */
    private static final String ADDRESS = "http://2.0.1.16:11456?wsdl";
    /**
     * 机器人ip
     */
    private static final String ROBOT_IP = "192.168.1.10";

    /**
     * 根据时间获取机器人任务列表信息
     */
    @Test
    public void getTaskFinishInfoByTime() {
        // 开始时间
        String startTime = "2022-09-19 00:00:00";
        // 结束时间
        String endTime = "2022-09-25 23:59:59";
        TaskFinishInfos result = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getTaskFinishInfoByTime(startTime, endTime);
        if (ObjectUtil.isNotEmpty(result)) {
            List<TaskFinishInfo> infos = result.getInfos();
            for (TaskFinishInfo info : infos) {
                System.out.print("任务id：" + info.getTaskId());
                System.out.print(" | 任务名称：" + info.getTaskName());
                System.out.print(" | 任务类型：" + info.getTaskType());
                System.out.print(" | 任务模板id：" + info.getTaskPathId());
                System.out.print(" | 完成巡检点位列表：" + info.getPointList());
                System.out.print(" | 开始时间：" + info.getStartTime());
                System.out.print(" | 结束时间：" + info.getEndTime());
                System.out.print(" | 任务状态：" + info.getFinishState());
                System.out.print(" | 执行机器人ip：" + info.getExcuteRobot());
                System.out.println();
            }
        }

    }

    /**
     * 根据任务id获取机器人完成任务数据信息
     */
    @Test
    public void getTaskExcuteInfoByTaskId() {
        // 任务id：T2018-12-10 14:43:53
        String taskId = "T20220920102157470";
        TaskExcuteInfos taskExcuteInfoByTaskId = WebServiceUtils.getWebService(ServicePortType.class, ADDRESS).getTaskExcuteInfoByTaskId(taskId);
        if (ObjectUtil.isNotEmpty(taskExcuteInfoByTaskId)) {
            List<TaskExcuteInfo> infos = taskExcuteInfoByTaskId.getInfos();
            for (TaskExcuteInfo info : infos) {
                System.out.print("任务id：" + info.getTaskId());
                System.out.print(" | 任务名称：" + info.getTaskName());
                System.out.print(" | 任务类型：" + info.getTaskType());
                System.out.print(" | 任务模板id：" + info.getTaskPathId());
                System.out.print(" | 巡检结果id：" + info.getTargetId());
                System.out.print(" | 巡检点位id：" + info.getPointId());
                System.out.print(" | 巡检点位名称：" + info.getPointName());
                System.out.print(" | 巡检点位类型：" + info.getPointType());
                System.out.print(" | 执行时间：" + info.getExcuteTime());
                System.out.print(" | 执行结果值：" + info.getExcuteValue());
                System.out.print(" | 执行结果状态：" + info.getExcuteState());
                System.out.print(" | 执行结果描述：" + info.getExcuteDesc());
                System.out.print(" | 高清图片：" + info.getHDPicture());
                System.out.print(" | 红外图片：" + info.getInfraredPicture());
                System.out.println();
            }
        }
    }
}
