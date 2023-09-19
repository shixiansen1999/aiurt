package com.aiurt.modules.deduplicate.handler;

import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.fastjson.JSON;
import io.swagger.util.Json;
import org.springframework.stereotype.Component;

/**
 * @author fgw
 */
@Component
public class ApprovalRequirementRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {


    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {
        // 提交是， 意见是否必填

        ActCustomTaskExt actCustomTaskExt = context.getActCustomTaskExt();
        String operationListJson = actCustomTaskExt.getOperationListJson();

        ActOperationEntity actOperationEntity = JSON.parseObject(operationListJson, ActOperationEntity.class);
    }
}
