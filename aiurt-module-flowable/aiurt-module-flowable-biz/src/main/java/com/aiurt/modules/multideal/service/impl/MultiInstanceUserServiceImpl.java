package com.aiurt.modules.multideal.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.multideal.service.IMultiInstanceUserService;
import com.alibaba.fastjson.JSONObject;
import org.flowable.engine.ProcessEngines;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fgw
 */
@Service
public class MultiInstanceUserServiceImpl implements IMultiInstanceUserService {

    /**
     * 获取当前活动的用户列表
     *
     * @param taskId
     * @return
     */
    @Override
    public List<String> getCurrentUserList(String taskId) {
        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskId(taskId)
                .singleResult();
        if (task == null) {
            throw new AiurtBootException(AiurtErrorEnum.TASK_ID_NOT_FOUND.getCode(), String.format(AiurtErrorEnum.TASK_ID_NOT_FOUND.getMessage(), taskId));
        }
        String variableName = FlowVariableConstant.ASSIGNEE_LIST + task.getTaskDefinitionKey();
        return  ProcessEngines.getDefaultProcessEngine().getTaskService().getVariable(taskId, variableName, List.class);
    }

    /**
     * @param nodeId
     * @param businessData
     * @param user
     * @return
     */
    @Override
    public List<String> getNextNodeUserList(String nodeId, JSONObject businessData, List<String> user) {
        return  user;
    }
}
