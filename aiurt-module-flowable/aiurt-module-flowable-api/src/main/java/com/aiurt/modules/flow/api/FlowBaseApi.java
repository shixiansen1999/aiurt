package com.aiurt.modules.flow.api;

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
}
