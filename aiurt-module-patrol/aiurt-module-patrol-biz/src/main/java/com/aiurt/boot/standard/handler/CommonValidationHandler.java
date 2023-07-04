package com.aiurt.boot.standard.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-30 16:40
 * @Description:
 */
public class CommonValidationHandler {
    /**
     * 校验“是否为巡视项目”为否的情况下，对应的字段填写的合规性。
     *
     * @param row    代表一行的数据，每个键值对表示一个字段名和其对应的列对象
     * @param column 要校验的列对象，这个对象包含了数据和其他元数据
     * @return ValidationResult 对象，表示校验的结果。如果校验通过，返回true，否则返回false以及对应的错误信息
     */
    public static ValidationResult validateForNoInspectionProject(Map<String, Column> row, Column column) {
        Column check = row.get("check");
        if (ObjectUtil.isNotEmpty(check)) {
            Object checkData = check.getData();
            if (PatrolConstant.FOU.equals(String.valueOf(checkData)) && ObjectUtil.isNotEmpty(column.getData())) {
                return new ValidationResult(false, String.format("是否为巡视项目为否时，%s字段不用填写", column.getName()));
            }
        }
        return new ValidationResult(true, null);
    }

}
