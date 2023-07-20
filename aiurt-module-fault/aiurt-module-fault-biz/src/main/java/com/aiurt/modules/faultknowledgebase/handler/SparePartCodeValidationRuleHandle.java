package com.aiurt.modules.faultknowledgebase.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-18 11:50
 * @Description:
 */
@Component("SparePartCodeValidationRuleHandle")
public class SparePartCodeValidationRuleHandle implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        if (ObjectUtil.isNotEmpty(column.getData())) {
            Object data = column.getData();
            String materialNameByCode = sysBaseApi.getMaterialNameByCode((String) data);
            if (materialNameByCode == null) {
                return new ValidationResult(false, String.format("系统不存在该备件编号"));
            }
        }

        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
