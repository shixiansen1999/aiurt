package com.aiurt.modules.cmd;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.Condition;
import org.flowable.engine.impl.el.UelExpressionCondition;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

/**
 * @author fgw
 */
public class ConditionExpressionCmd implements Command<Boolean> {

    private ExecutionEntity execution;

    private String conditionExpression;

    private String sequenceFlowId;

    public ConditionExpressionCmd(ExecutionEntity execution, String conditionExpression, String sequenceFlowId) {
        this.conditionExpression = conditionExpression;
        this.execution = execution;
        this.sequenceFlowId = sequenceFlowId;
    }


    @Override
    public Boolean execute(CommandContext commandContext) {
        Expression expression = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager().createExpression(conditionExpression);
        Condition condition = new UelExpressionCondition(expression);
        boolean evaluate = condition.evaluate(sequenceFlowId, execution);
        return evaluate;
    }
}
