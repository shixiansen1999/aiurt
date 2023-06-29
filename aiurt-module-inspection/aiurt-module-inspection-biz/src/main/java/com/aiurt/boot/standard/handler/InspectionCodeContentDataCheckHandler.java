package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-29 15:05
 * @Description: 作用在数据校验表达式字段上
 */
@Component("InspectionCodeContentDataCheckHandler")
public class InspectionCodeContentDataCheckHandler implements RowValidationRule {
    /**
     * 检查值类型是输入项时，数据校验表达式字段必填
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Column statusItem = row.get("status_item");

        if (ObjectUtil.isNotEmpty(statusItem)) {
            Object statusItemData = statusItem.getData();

            // 判断 "status_item" 列的数据是否是选择项或输入项
            boolean isSelectableOrInput = InspectionConstant.INPUT_ITEM_3.equals(String.valueOf(statusItemData));

            // 判断当前列的数据是否为空
            boolean isColumnDataEmpty = ObjectUtil.isEmpty(column.getData());

            if (isSelectableOrInput && isColumnDataEmpty) {
                return new ValidationResult(false, "检查值类型是输入项时，数据校验表达式字段必填");
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
