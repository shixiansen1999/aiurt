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
 * @create: 2023-06-29 15:01
 * @Description: 作用在关联字典字段上
 */
@Component("InspectionCodeContentDictCodeHandler")
public class InspectionCodeContentDictCodeHandler implements RowValidationRule {
    /**
     * 检查值类型是选择项时，关联字段字段必填
     *
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
            boolean isSelectableOrInput = InspectionConstant.SELECT_ITEM_2.equals(String.valueOf(statusItemData));

            // 判断当前列的数据是否为空
            if (isSelectableOrInput && ObjectUtil.isEmpty(column.getData())) {
                return new ValidationResult(false, "检查值类型是选择项时，关联字典字段必填");
            }

            if (!isSelectableOrInput && ObjectUtil.isNotEmpty(column.getData())) {
                return new ValidationResult(false, "检查值类型不是选择项，关联字典字段不用填写");
            }
        }

        ValidationResult validationResult = CommonValidationHandler.validateForNoInspectionProject(row, column);
        if (!validationResult.isValid()) {
            return validationResult;
        }

        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
