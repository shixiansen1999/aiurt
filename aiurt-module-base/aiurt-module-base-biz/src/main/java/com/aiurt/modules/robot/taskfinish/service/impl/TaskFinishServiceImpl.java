package com.aiurt.modules.robot.taskfinish.service.impl;


import com.aiurt.common.util.webservice.WebServiceUtils;
import com.aiurt.modules.robot.taskfinish.service.TaskFinishService;
import com.aiurt.modules.robot.taskfinish.wsdl.ServicePortType;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskExcuteInfos;
import com.aiurt.modules.robot.taskfinish.wsdl.TaskFinishInfos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/219:03
 */
@Service
public class TaskFinishServiceImpl implements TaskFinishService {
    @Value("${robot.wsdl.address}")
    private String address;


    /**
     * 根据时间获取机器人任务列表信息
     *
     * @param startTime 开始时间,格式2018-12-14 09:36:23
     * @param endTime   结束时间,格式2018-12-15 09:36:23
     * @return TaskFinishInfos
     */
    @Override
    public TaskFinishInfos getTaskFinishInfoByTime(String startTime, String endTime) {
        TaskFinishInfos result = WebServiceUtils.getWebService(ServicePortType.class, address).getTaskFinishInfoByTime(startTime, endTime);
        return result;
    }

    /**
     * 根据任务id获取机器人完成任务数据信息
     *
     * @param taskId 任务id
     * @return TaskExcuteInfos
     */
    @Override
    public TaskExcuteInfos getTaskExcuteInfoByTaskId(String taskId) {
        TaskExcuteInfos result = WebServiceUtils.getWebService(ServicePortType.class, address).getTaskExcuteInfoByTaskId(taskId);
        return result;
    }
}
