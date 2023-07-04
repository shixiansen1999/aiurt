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
 * @create: 2023-06-29 15:01
 * @Description: 作用在关联字典字段上
 */
@Component("PatrolStandardItemDictCodeHandler")
public class PatrolStandardItemDictCodeHandler implements RowValidationRule {
    /**
     * 检查值类型是选择项时，关联字段字段必填
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
            boolean isSelectableOrInput = PatrolConstant.SELECT_ITEM_2.equals(String.valueOf(inputTypeData));

            // 判断当前列的数据是否为空
            boolean isColumnDataEmpty = ObjectUtil.isEmpty(column.getData());

            if (isSelectableOrInput && isColumnDataEmpty) {
                return new ValidationResult(false, "检查值类型是选择项时，关联字典字段必填");
            }

            if (!isSelectableOrInput &&  ObjectUtil.isNotEmpty(column.getData())) {
                return new ValidationResult(false, "检查值类型不是选择项时，关联字典字段不用填写");
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
