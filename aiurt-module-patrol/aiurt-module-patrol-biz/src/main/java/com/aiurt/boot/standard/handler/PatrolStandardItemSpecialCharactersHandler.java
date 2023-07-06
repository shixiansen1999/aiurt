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
 * @create: 2023-06-29 14:47
 * @Description: 作用在特殊字符输入（special_characters）字段上
 */
@Component("PatrolStandardItemSpecialCharactersHandler")
public class PatrolStandardItemSpecialCharactersHandler implements RowValidationRule {
    /**
     * 检查值类型为特殊字符输入时，特殊字符输入字段必填
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

            // 判断 "input_type" 列的数据是否是特殊字符输入
            boolean isSelectableOrInput = PatrolConstant.SPECIAL_CHARACTERS_4.equals(String.valueOf(inputTypeData));

            // 判断当前列的数据是否为空
            boolean isColumnDataEmpty = ObjectUtil.isEmpty(column.getData());

            if (isSelectableOrInput && isColumnDataEmpty) {
                return new ValidationResult(false, "检查值类型是特殊字符输入时，特殊字符输入字段必填");
            }

            if (!isSelectableOrInput && ObjectUtil.isNotEmpty(column.getData())) {
                return new ValidationResult(false, "检查值类型不是特殊字符输入时，数特殊字符输入字段不用填写");
            }
        }

        // 是否为巡视项目为否时，特殊字符输入不用填写
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
