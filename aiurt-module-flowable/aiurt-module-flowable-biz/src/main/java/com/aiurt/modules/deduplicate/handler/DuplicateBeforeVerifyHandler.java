package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @author gaowei
 */
@Slf4j
@Service
public class DuplicateBeforeVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {
    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        ActCustomModelExt actCustomModelExt = context.getActCustomModelExt();
        if (Objects.isNull(actCustomModelExt)) {
            if (log.isDebugEnabled()) {
                log.debug("审批去重，该流程没有配置信息");
            }
            context.setContinueChain(false);
        }else {
            Integer isDeduplicate = Optional.ofNullable(actCustomModelExt.getIsDedulicate()).orElse(0);
            if (isDeduplicate == 0 ) {
                if (log.isDebugEnabled()) {
                    log.debug("审批去重，该流程不开始审批去重");
                }
                context.setContinueChain(false);
                return;
            }
        }
    }
}
