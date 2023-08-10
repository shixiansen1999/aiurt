package com.aiurt.modules.complete.dto;

import lombok.Data;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.io.Serializable;

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

}
