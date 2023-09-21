package com.aiurt.modules.online.businessdata.service;

import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程中间业务数据
 * @Author: aiurt
 * @Date:   2022-10-27
 * @Version: V1.0
 */
public interface IActCustomBusinessDataService extends IService<ActCustomBusinessData> {

    /**
     * 根据任务id或者流程实例id查询保存的业务数据
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return
     */
    ActCustomBusinessData queryByProcessInstanceId(String processInstanceId, String taskId);

    /**
     * 根据节点id 或者任务id或者流程实例id查询保存的业务数据
     * @param processInstanceId
     * @param taskId
     * @param nodeId
     * @return
     */
    ActCustomBusinessData queryOne(String processInstanceId, String taskId, String nodeId);
}
