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
 * @create: 2023-06-29 14:47
 * @Description: 作用在检查值类型字段上
 */
@Component("InspectionCodeContentStatusItemHandler")
public class InspectionCodeContentStatusItemHandler implements RowValidationRule {
    /**
     * 是否检查项为是时，检查值类型字段必填
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Column type = row.get("type");
        if (ObjectUtil.isNotEmpty(type)) {
            Object typeData = type.getData();
            if (ObjectUtil.isNotEmpty(typeData) && InspectionConstant.SHI.equals(String.valueOf(typeData)) && ObjectUtil.isEmpty(column.getData())) {
                return new ValidationResult(false, "是否检查项为是时，检查值类型字段必填");
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
