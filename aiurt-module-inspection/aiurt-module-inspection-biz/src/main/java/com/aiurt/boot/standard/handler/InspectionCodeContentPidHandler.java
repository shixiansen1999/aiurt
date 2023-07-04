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
 * @create: 2023-06-29 14:35
 * @Description: 作用在父级字段上
 */
@Component("InspectionCodeContentPidHandler")
public class InspectionCodeContentPidHandler implements RowValidationRule {
    /**
     * 如果层级类型是一级，那么父级不是必填的
     * 如果层级类型是子级，那么父级是必填的
     *
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        if (ObjectUtil.isEmpty(row.get("has_child"))) {
            return new ValidationResult(true, null);
        }

        String levelType = getLevelType(row);
        if (levelType == null) {
            return new ValidationResult(true, null);
        }

        if (isChildLevel(levelType) && isEmptyParentColumn(column)) {
            return new ValidationResult(false, "层级类型是子级时，父级是必填的");
        }

        if (isFirstLevel(levelType) && ObjectUtil.isNotEmpty(column.getData())) {
            return new ValidationResult(false, "层级类型是一级时，父级不用填写");
        }

        return new ValidationResult(true, null);
    }

    private String getLevelType(Map<String, Column> row) {
        return String.valueOf(row.get("has_child").getData());
    }

    private boolean isEmptyParentColumn(Column column) {
        return ObjectUtil.isEmpty(column.getData());
    }

    private boolean isFirstLevel(String levelType) {
        return InspectionConstant.LEVEL_TYPE_0.equals(levelType);
    }

    private boolean isChildLevel(String levelType) {
        return InspectionConstant.LEVEL_TYPE_1.equals(levelType);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
