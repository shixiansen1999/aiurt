package com.aiurt.modules.flow.api;

import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.StartBpmnImportDTO;
import com.aiurt.modules.flow.dto.TaskCompleteDTO;
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


    /**
     * 启动流程，导入
     * @param startBpmnImportDTO
     */
    void startBpmnWithImport(StartBpmnImportDTO startBpmnImportDTO);

    /**
     * 删除业务数据，终止流程
     * @param userName
     * @param businessKey
     * @param delReason
     */
    void delProcess(String userName, String businessKey, String delReason);

    /**
     * 提交任务
     * @param taskCompleteDTO
     */
    void completeTask(TaskCompleteDTO taskCompleteDTO);
}
