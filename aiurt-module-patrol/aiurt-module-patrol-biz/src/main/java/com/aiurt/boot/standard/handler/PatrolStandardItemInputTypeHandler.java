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
 * @Description: 作用在数据填写类型字段上
 */
@Component("PatrolStandardItemInputTypeHandler")
public class PatrolStandardItemInputTypeHandler implements RowValidationRule {
    /**
     * 是否为巡视项目为是时，检查值类型字段必填
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        Column check = row.get("check");
        if (ObjectUtil.isEmpty(check)) {
            return new ValidationResult(true, null);
        }

        Object checkData = check.getData();
        if (ObjectUtil.isEmpty(checkData)) {
            return new ValidationResult(true, null);
        }

        if (PatrolConstant.SHI.equals(String.valueOf(checkData)) && ObjectUtil.isEmpty(column.getData())) {
            return new ValidationResult(false, "是否为巡视项目为是时，检查值类型字段必填");
        }

        ValidationResult validationResult = CommonValidation.validateForNoInspectionProject(row, column);
        if (!validationResult.isValid()) {
            return validationResult;
        }

        // 是否为巡视项目为否时，数据填写类型不用填写
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
