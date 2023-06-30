package com.aiurt.boot.standard.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowDataSetValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-06-30 10:15
 * @Description: 作用与parent_id字段上
 */
@Component("PatrolStandardItemLevelTypeAndCheck")
public class PatrolStandardItemLevelTypeAndCheck implements RowDataSetValidationRule {

    /**
     * 当层级类型是一级并且是否为巡检项目为是时，该节点不可以有子级
     * @param rows
     * @param row
     * @param column
     * @return
     */
    @Override
    public ValidationResult validate(List<Map<String, Column>> rows, Map<String, Column> row, Column column) {
        if (ObjectUtil.isEmpty(column.getData())) {
            return new ValidationResult(true, null);
        }

        if (isFirstLevelWithProject(row) && isChildNode(getContentColumnName(row), rows)) {
            return new ValidationResult(false, "当层级类型为一级并且是否为巡视项目为是时，该节点不可以有子级");
        }

        return new ValidationResult(true, null);
    }

    /**
     * 检查指定的行是否为一级项目
     * @param row 行数据
     * @return 如果行为一级项目，则返回 true，否则返回 false
     */
    private boolean isFirstLevelWithProject(Map<String, Column> row) {
        return PatrolConstant.LEVEL_TYPE_0.equals(getData(row, "hierarchy_type")) && isPatrolProject(row);
    }

    /**
     * 检查指定的行是否为巡检项目
     * @param row 行数据
     * @return 如果行为巡检项目，则返回 true，否则返回 false
     */
    private boolean isPatrolProject(Map<String, Column> row) {
        return PatrolConstant.SHI.equals(getData(row, "check"));
    }

    /**
     * 获取指定行的指定列数据
     * @param row 行数据
     * @param columnName 列名
     * @return 列数据
     */
    private String getData(Map<String, Column> row, String columnName) {
        Column column = row.get(columnName);
        return column != null ? String.valueOf(column.getData()) : null;
    }

    /**
     * 获取指定行的内容列名
     * @param row 行数据
     * @return 内容列名
     */
    private String getContentColumnName(Map<String, Column> row) {
        return getData(row, "content");
    }

    /**
     * 检查指定的内容列是否为其他行的子节点
     * @param contentColumn 内容列名
     * @param rows 所有行数据
     * @return 如果内容列名为其他行的子节点，则返回 true，否则返回 false
     */
    private boolean isChildNode(String contentColumn, List<Map<String, Column>> rows) {
        if (CollUtil.isEmpty(rows)) {
            return false;
        }
        for (Map<String, Column> row : rows) {
            if (isSecondLevelWithParent(row, contentColumn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查指定的行是否为二级项目且父节点名称与指定的名称相同
     * @param row 行数据
     * @param parentName 父节点名称
     * @return 如果行为二级项目且父节点名称与指定的名称相同，则返回 true，否则返回 false
     */
    private boolean isSecondLevelWithParent(Map<String, Column> row, String parentName) {
        return PatrolConstant.LEVEL_TYPE_1.equals(getData(row, "hierarchy_type"))
                && parentName.equals(getData(row, "parent_id"));
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
