package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-29 14:53
 * @Description: 作用在检查值是否必填字段上
 */
@Component("PatrolStandardItemRequiredHandler")
public class PatrolStandardItemRequiredHandler implements RowValidationRule {
    /**
     * 检查值类型是选择项或输入项时，检查值是否必填字段必填
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Column inputTypeColumn = row.get("input_type");

        if (ObjectUtil.isNotEmpty(inputTypeColumn)) {
            Object inputTypeData = inputTypeColumn.getData();

            // 判断 "input_type" 列的数据是否是选择项或输入项
            boolean isSelectableOrInput = PatrolConstant.SELECT_ITEM_2.equals(String.valueOf(inputTypeData))
                    || PatrolConstant.INPUT_ITEM_3.equals(String.valueOf(inputTypeData));

            // 判断当前列的数据是否为空
            boolean isColumnDataEmpty = ObjectUtil.isEmpty(column.getData());

            if (isSelectableOrInput && isColumnDataEmpty) {
                return new ValidationResult(false, "检查值类型是选择项或输入项时，检查值是否必填字段必填");
            }
        }

        ValidationResult validationResult = CommonValidation.validateForNoInspectionProject(row, column);
        if (!validationResult.isValid()) {
            return validationResult;
        }

        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
