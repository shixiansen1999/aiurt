package com.aiurt.modules.user.filters;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.common.pipeline.FlowHandlerChain;
import com.aiurt.modules.user.dto.SelectUserContext;
import org.springframework.stereotype.Service;

/**
 * @author fgw
 */
@Service
public class DefaultNullUserHandler extends AbstractFlowHandler<SelectUserContext> {


    /**
     * 实际任务处理
     *
     * @param context
     * @param chain
     */
    @Override
    public void doHandle(SelectUserContext context, FlowHandlerChain chain) {
        super.doHandle(context, chain);
    }

    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {

    }
}
