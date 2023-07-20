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
@Component("SparePartNameValidationRuleHandle")
public class SparePartNameValidationRuleHandle implements RowValidationRule {
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {


        Column sparePartCode = row.get("spare_part_code");
        Object data = sparePartCode.getData();
        if (ObjectUtil.isNotEmpty(data)) {
            if (ObjectUtil.isEmpty(column.getData())) {
                return new ValidationResult(false, String.format("备件名称不能为空"));
            }else {
                String materialNameByCode = sysBaseApi.getMaterialNameByCode((String) data);
                if (!materialNameByCode.equals((String) column.getData())) {
                    return new ValidationResult(false, String.format("备件名称和备件编号不匹配"));
                }
            }
        }else {
            if (ObjectUtil.isNotEmpty(column.getData())) {
                return new ValidationResult(false, String.format("备件编号不能为空"));
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
