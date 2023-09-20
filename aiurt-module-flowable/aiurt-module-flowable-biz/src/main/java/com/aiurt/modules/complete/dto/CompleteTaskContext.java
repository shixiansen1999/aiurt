package com.aiurt.modules.complete.dto;

import lombok.Data;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author fgw
 * @desc 提交服务上下文
 */
@Data
public class CompleteTaskContext implements Serializable {
    private static final long serialVersionUID = 3551932337089292933L;

    private FlowCompleteReqDTO flowCompleteReqDTO;

    /**
     * 当前活动
     */
    private Task currentTask;

    /**
     * 当前流程实例
     */
    private ProcessInstance processInstance;

    /**
     * 流程变量
     */
    private Map<String, Object> variableData;

    /**
     * 执行行实例
     */
    private ExecutionEntity executionEntity;

    /**
     * 办理规则
     */
    private String multiApprovalRule;

    /**
     * 目标节点
     */
    private List<FlowElement> targetFlowElement;

    /**
     * 是否提交任务
     */
    private Boolean completeTask;
}
