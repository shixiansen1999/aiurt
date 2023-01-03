package com.aiurt.modules.flow.api;

import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;

/**
 * @author fgw
 * @date 2022-12-28
 */
public interface FlowBaseApi {

    /**
     * 查询当前任务的权限信息（页面，按钮权限）
     * @param processInstanceId
     * @param taskId
     * @return
     */
    TaskInfoDTO viewRuntimeTaskInfo(String processInstanceId, String taskId);


    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    void startAndTakeFirst(StartBpmnDTO startBpmnDTO);
}
