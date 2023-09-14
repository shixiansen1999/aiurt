package com.aiurt.modules.deduplicate.context;

import com.aiurt.modules.common.pipeline.context.AbstractFlowContext;
import com.aiurt.modules.common.pipeline.selector.HandlerSelector;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import lombok.Getter;
import lombok.Setter;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
@Setter
@Getter
public class FlowDeduplicateContext extends AbstractFlowContext {

    /**
     * 流程全局属性
     */
    private ActCustomModelExt actCustomModelExt;

    /**
     * 流程提醒
     */
    private ProcessInstance processInstance;

    /**
     * 当前任务
     */
    private Task task;

    /**
     * 历史任务
     */
    private List<HistoricTaskInstance> historicTaskInstanceList;


    /**
     * 是否允许继续执行下一个处理器
     */
    private Boolean continueChain;

    /**
     * 当前任务的扩展属性
     */
    private ActCustomTaskExt actCustomTaskExt;


    public FlowDeduplicateContext(HandlerSelector selector) {
        super(selector);
    }

    /**
     * 是否继续链
     *
     * @return
     */
    @Override
    public boolean continueChain() {
        if (Objects.nonNull(this.continueChain)) {
            return this.continueChain;
        }
        return true;
    }
}
