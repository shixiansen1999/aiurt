package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程定义节点属性
 * @Author: aiurt
 * @Date:   2022-08-02
 * @Version: V1.0
 */
public interface IActCustomTaskExtService extends IService<ActCustomTaskExt> {
    /**
     * 查询指定的流程任务扩展对象。
     * @param processDefinitionId 流程引擎的定义Id。
     * @param taskId              流程引擎的任务唯一标识。
     * @return 查询结果。
     */
    ActCustomTaskExt getByProcessDefinitionIdAndTaskId(String processDefinitionId, String taskId);
}
