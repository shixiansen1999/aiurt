package com.aiurt.modules.remind.handlers;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>提醒规则校验</p>
 * @author fgw
 */
@Component
public class RemindRuleVerifyHandler extends AbstractFlowHandler<FlowRemindContext> {


    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {
        // 流程未结束
        HistoricProcessInstance processInstance = context.getProcessInstance();

        Date endTime = processInstance.getEndTime();

        // 已结束,
        if (Objects.nonNull(endTime)) {
            context.setContinueChain(false);
            throw new AiurtBootException(AiurtErrorEnum.NEXT_NODE_IS_END.getCode(), AiurtErrorEnum.NEXT_NODE_IS_END.getMessage());
        }

        ActCustomModelExt actCustomModelExt = context.getActCustomModelExt();
        Integer isRemind = Optional.ofNullable(actCustomModelExt.getIsRemind()).orElse(0);
        // 不催办
        if (isRemind == 0) {
            context.setContinueChain(false);
        }

        ActCustomRemindRecord lastRemindRecord = context.getLastRemindRecord();
        // 仅发起人可催办流程，且可以对每个流程节点每 5 分钟催办一次；
        if (Objects.nonNull(lastRemindRecord)) {
            // 上次催办时间
            Date lastRemindTime = Optional.ofNullable(lastRemindRecord.getLastRemindTime()).orElse(new Date());

            long between = DateUtil.between(lastRemindTime, new Date(), DateUnit.MINUTE);

            if (between < 5) {
                context.setContinueChain(false);
                throw new AiurtBootException("催办需间隔5分钟， 请"+(5-between)+"分钟后重试");
            }
        }
    }
}
