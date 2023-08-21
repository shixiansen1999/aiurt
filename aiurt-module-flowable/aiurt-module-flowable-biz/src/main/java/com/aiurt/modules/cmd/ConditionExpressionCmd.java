package com.aiurt.modules.cmd;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.util.Map;

/**
 * @author fgw
 */
public class ConditionExpressionCmd implements Command<Boolean> {

    private ExecutionEntity execution;

    private String conditionExpression;

    private Map<String,Object> variables;

    public ConditionExpressionCmd(ExecutionEntity execution, String conditionExpression, Map<String,Object> variables) {
        this.conditionExpression = conditionExpression;
        this.execution = execution;
        this.variables = variables;
    }


    @Override
    public Boolean execute(CommandContext commandContext) {
        Expression expression = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager().createExpression(conditionExpression);

        execution.setTransientVariables(variables);
        Object result = expression.getValue(execution);

        if (result == null) {
            return false;
        }
        if (!(result instanceof Boolean)) {
            return false;
        }
        return (Boolean) result;
    }
}
