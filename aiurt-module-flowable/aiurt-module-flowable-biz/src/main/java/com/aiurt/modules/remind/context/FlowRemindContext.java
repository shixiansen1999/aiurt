package com.aiurt.modules.remind.context;

import com.aiurt.modules.common.pipeline.context.AbstractFlowContext;
import com.aiurt.modules.common.pipeline.selector.HandlerSelector;
import lombok.Getter;
import lombok.Setter;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Objects;

/**
 * <p>催办上下文</p>
 * @author fgw
 */
@Setter
@Getter
public class FlowRemindContext extends AbstractFlowContext {

    private String processInstanceId;

    /**
     * 流程实例
     */
    private ProcessInstance processInstance;


    /**
     * 催办用户
     */
    private List<String> userList;

    /**
     * 是否继续执行下一任务链
     */
    private Boolean continueChain;


    public FlowRemindContext(HandlerSelector selector) {
        super(selector);
    }

    /**
     * 是否继续链
     *
     * @return
     */
    @Override
    public boolean continueChain() {

        processInstance.getBusinessKey();
        if (Objects.nonNull(this.continueChain)) {
            return this.continueChain;
        }
        return true;
    }
}
