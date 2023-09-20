package com.aiurt.modules.user.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.common.pipeline.FlowHandlerChain;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
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
        if (context.getHandlerSelector().matchHandler(this.getClass().getSimpleName())) {
            ActCustomUser customUser = context.getCustomUser();

            // 是否开启任务
            if (CollUtil.isEmpty(context.getUserList())) {
                // 业务处理
                handle(context);
            }
        }

        if (context.continueChain()) {
            // 执行下一个过滤器
            chain.fireNext(context);
        }
    }

    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {

    }
}
