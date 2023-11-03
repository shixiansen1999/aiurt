package com.aiurt.modules.el.funtion;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.function.VariableContainsAnyExpressionFunction;
import org.flowable.common.engine.impl.el.function.VariableContainsExpressionFunction;

import java.util.Collection;
import java.util.List;

/**
 *
 * 自定义表达式
 * @author fgw
 */
public class CustomVariableContainsAnyExpressionFunction extends VariableContainsAnyExpressionFunction {

    public CustomVariableContainsAnyExpressionFunction () {
        super();
    }


    @SuppressWarnings({ "rawtypes"})
    public static boolean containsAny(VariableContainer variableContainer, String variableName, Object... values) {
        Object variableValue = getVariableValue(variableContainer, variableName);
        if (variableValue != null) {
            if (variableValue instanceof String) {
                String variableStringValue = (String) variableValue;
                for (Object value : values) {
                    String stringValue = (String) value;
                    if (StrUtil.isNotBlank(variableStringValue) && StrUtil.isNotBlank(stringValue)) {
                        List<String> variableList = StrUtil.split(variableStringValue, ',');
                        List<String> valueList = StrUtil.split(stringValue, ',');
                        if (valueList.size() == 1 && variableList.size() == 1) {
                            if (StringUtils.contains(variableStringValue, stringValue)) {
                                return true;
                            }
                        }
                        // 比较
                        Collection<String> intersection = CollUtil.intersection(variableList, valueList);
                        return CollUtil.isNotEmpty(intersection);
                    }
                    if (StringUtils.contains(variableStringValue, stringValue)) {
                        return true;
                    }
                }
                return false;

            } else if (variableValue instanceof Collection) {
                Collection collectionVariableValue = (Collection) variableValue;
                for (Object value : values) {
                    if (VariableContainsExpressionFunction.collectionContains(collectionVariableValue, value)) {
                        return true;
                    }
                }
                return false;

            } else if (variableValue instanceof ArrayNode) {
                ArrayNode arrayNodeVariableValue = (ArrayNode) variableValue;
                for (Object value : values) {
                    if (VariableContainsExpressionFunction.arrayNodeContains(arrayNodeVariableValue, value)) {
                        return true;
                    }
                }
                return false;

            }
        }

        return false;
    }
}
