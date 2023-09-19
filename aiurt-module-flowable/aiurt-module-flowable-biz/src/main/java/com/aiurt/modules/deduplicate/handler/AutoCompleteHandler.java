package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fgw
 */
@Slf4j
@Component
public class AutoCompleteHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {


    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        // 自动提交，判断是否正常逻辑，不管是否，都是满配选人
        // 判断是否
    }
}
