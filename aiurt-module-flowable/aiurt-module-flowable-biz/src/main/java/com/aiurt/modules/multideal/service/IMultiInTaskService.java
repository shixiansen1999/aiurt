package com.aiurt.modules.multideal.service;

import org.flowable.task.api.Task;

/**
 * @author fgw
 * @desc  判断是否多实例任务且不是多实例的最后一步
 */
public interface IMultiInTaskService {

    /**
     * 判断是否为多实例任务
     * @param task
     * @return
     */
    Boolean isMultiInTask(Task task);

    /**
     * 判断是否多实例任务且不是多实例的最后一步
     * 是多实例任务且不是多实例的最后一步  返回true
     * 否则返回false
     * @param task
     * @return
     */
    Boolean areMultiInTask(Task task);


    /**
     * 判断是否为多实例任务
     * @param nodeId
     * @param definitionId
     * @return
     */
    Boolean isMultiInTask(String nodeId, String definitionId);
}
