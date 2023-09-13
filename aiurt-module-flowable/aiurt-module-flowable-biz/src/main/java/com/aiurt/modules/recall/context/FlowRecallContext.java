package com.aiurt.modules.recall.context;

import com.aiurt.modules.common.pipeline.context.AbstractFlowContext;
import com.aiurt.modules.common.pipeline.selector.HandlerSelector;

import java.util.Objects;

/**
 * @author fgw
 */
public class FlowRecallContext extends AbstractFlowContext {


    /**
     * 是否继续执行下一任务链
     */
    private Boolean continueChain;


    public FlowRecallContext(HandlerSelector selector) {
        super(selector);
    }


    /**
     * 是否继续执行下一个链
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
