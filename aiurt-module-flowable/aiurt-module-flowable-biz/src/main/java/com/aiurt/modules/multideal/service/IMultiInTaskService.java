package com.aiurt.modules.multideal.service;

import com.aiurt.modules.flow.dto.ProcessParticipantsInfoDTO;
import com.aiurt.modules.multideal.dto.AddReduceMultiInstanceDTO;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * @author fgw
 * @desc  判断是否多实例任务且不是多实例的最后一步
 */
public interface IMultiInTaskService {

    /**
     * 判断是否为多实例任务，根据流程多实例的流程变量判断
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
     * 判断是否为多实例任务， 根据流程的定义配置判断
     * @param nodeId
     * @param definitionId
     * @return
     */
    Boolean isMultiInTask(String nodeId, String definitionId);


    /**
     * 判断当前任务是否需要提交了
     * @param task
     * @return
     */
    Boolean isCompleteTask(Task task);

    /**
     * 加签
     * @param addReduceMultiInstanceDTO
     */
    void addMultiInstance(AddReduceMultiInstanceDTO addReduceMultiInstanceDTO);

    /**
     * 减签
     * @param addReduceMultiInstanceDTO
     */
    void reduceMultiInstance(AddReduceMultiInstanceDTO addReduceMultiInstanceDTO);

    /**
     * 查询减签的人员信息
     * @param taskId
     * @return
     */
    List<ProcessParticipantsInfoDTO> getReduceMultiUser(String taskId);
}
