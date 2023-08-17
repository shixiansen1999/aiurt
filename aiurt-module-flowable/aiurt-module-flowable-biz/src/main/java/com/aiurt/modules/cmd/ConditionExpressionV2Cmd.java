package com.aiurt.modules.cmd;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.util.Map;

public class ConditionExpressionV2Cmd implements Command<Boolean> {
    private String conditionExpression;
    private Map<String, Object> data;

    public ConditionExpressionV2Cmd(String conditionExpression, Map<String, Object> data) {
        this.conditionExpression = conditionExpression;
        this.data = data;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        ExpressionManager expressionManager = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
        Expression expression = expressionManager.createExpression(conditionExpression);
        DelegateExecution delegateExecution = new ExecutionEntityImpl();

        //  必须添加该属性，否则报execution Id is not empty，
        data.put("variableContainer", delegateExecution);
        delegateExecution.setTransientVariables(data);

        Object result = expression.getValue(delegateExecution);
        if (result == null) {
            return false;
        }
        if (!(result instanceof Boolean)) {
            return false;
        }
        return (Boolean) result;
    }
}
