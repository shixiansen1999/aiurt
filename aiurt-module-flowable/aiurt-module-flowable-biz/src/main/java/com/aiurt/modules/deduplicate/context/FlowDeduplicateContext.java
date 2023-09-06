package com.aiurt.modules.deduplicate.context;

import com.aiurt.modules.common.pipeline.context.AbstractFlowContext;
import com.aiurt.modules.common.pipeline.selector.HandlerSelector;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author fgw
 */
@Setter
@Getter
public class FlowDeduplicateContext extends AbstractFlowContext {

    private Boolean continueChain;


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
