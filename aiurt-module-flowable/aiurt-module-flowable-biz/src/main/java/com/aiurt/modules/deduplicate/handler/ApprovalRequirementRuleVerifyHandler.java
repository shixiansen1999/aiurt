package com.aiurt.modules.deduplicate.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.flow.constants.FlowApprovalType;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.fastjson.JSON;
import io.swagger.util.Json;
import kotlin.OptIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
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

        List<ActOperationEntity> actOperationEntityList = JSON.parseArray(operationListJson, ActOperationEntity.class);

        // 查询提交的按钮
        if (CollUtil.isEmpty(actOperationEntityList)) {
            return;
        }

        //
        List<ActOperationEntity> agreeList = actOperationEntityList.stream().filter(actOperationEntity -> StrUtil.equalsIgnoreCase(actOperationEntity.getType(), FlowApprovalType.AGREE))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(agreeList)) {
            return;
        }

        ActOperationEntity actOperationEntity = agreeList.get(0);

        // 是否必填
        Boolean mustRemark = Optional.ofNullable(actOperationEntity.getMustRemark()).orElse(Boolean.TRUE);

        // 是否有填写意见
        Boolean hasRemark = Optional.ofNullable(actOperationEntity.getHasRemark()).orElse(Boolean.TRUE);

        // 是否有填写意见
        if (hasRemark) {
            // 是否必填
            context.setContinueChain(!mustRemark);
        }else {
            context.setContinueChain(true);
        }

    }
}
