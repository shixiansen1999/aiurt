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
        Column hasChildColumn = row.get("has_child");
        if (ObjectUtil.isNotEmpty(hasChildColumn)) {
            Object levelType = hasChildColumn.getData();
            if (ObjectUtil.isNotEmpty(levelType) && InspectionConstant.LEVEL_TYPE_1.equals(String.valueOf(levelType)) && ObjectUtil.isEmpty(column.getData())) {
                return new ValidationResult(false, "层级类型是子级时，父级是必填的");
            }
        }
        return new ValidationResult(true, null);
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
