package com.aiurt.modules.flow.feginApi;

import com.aiurt.modules.flow.api.FlowBaseApi;
import com.aiurt.modules.flow.dto.StartBpmnDTO;
import com.aiurt.modules.flow.dto.TaskInfoDTO;
import com.aiurt.modules.flow.service.FlowApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fgw
 * @Data 2022-12-28
 */
@Slf4j
@Component
public class FlowBaseApiImpl implements FlowBaseApi {

    @Autowired
    private FlowApiService flowApiService;

    /**
     * 查询当前任务的权限信息（页面，按钮权限）
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    @Override
    public TaskInfoDTO viewRuntimeTaskInfo(String processInstanceId, String taskId) {
        TaskInfoDTO taskInfoDTO = new TaskInfoDTO();
        try {
             taskInfoDTO = flowApiService.viewRuntimeTaskInfo(null, processInstanceId, taskId);
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        }
        return taskInfoDTO;
    }

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    @Override
    public void startAndTakeFirst(StartBpmnDTO startBpmnDTO) {
        flowApiService.startAndTakeFirst(startBpmnDTO);
    }
}
