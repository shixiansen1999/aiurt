package com.aiurt.modules.flow.service;

import com.aiurt.modules.flow.dto.StartBpmnDTO;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * 流程引擎API的接口封装服务。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public interface FlowApiService {


    /**
     * 启动流程
     * @param startBpmnDTO
     * @return
     */
    public ProcessInstance start(StartBpmnDTO startBpmnDTO);

    /**
     * 启动流程实例，如果当前登录用户为第一个用户任务的指派者，或者Assginee为流程启动人变量时，
     * 则自动完成第一个用户任务。
     *
     * @param startBpmnDTO 流程定义Id。
     * @return 新启动的流程实例。
     */
    ProcessInstance startAndTakeFirst(StartBpmnDTO startBpmnDTO);



}
