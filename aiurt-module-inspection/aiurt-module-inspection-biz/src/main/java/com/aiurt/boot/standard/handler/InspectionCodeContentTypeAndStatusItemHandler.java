package com.aiurt.boot.standard.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowDataSetValidationRule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:wgp
 * @create: 2023-06-30 10:15
 * @Description: 作用与parent_id字段上
 */
@Component("InspectionCodeContentTypeAndStatusItemHandler")
public class InspectionCodeContentTypeAndStatusItemHandler implements RowDataSetValidationRule {

    /**
     * 当层级类型是一级并且是否为巡检项目为是时，该节点不可以有子级
     *
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

        String hasChild = getData(row, "has_child");

        if (ObjectUtil.isNotEmpty(hasChild)) {
            if (InspectionConstant.LEVEL_TYPE_1.equals(hasChild) && parentLevelIsSecondary(column.getData(), rows)) {
                return new ValidationResult(false, "当前数据的父节点的层级类型是子级，在业务上不允许");
            }
        }

        if (isFirstLevelWithProject(row) && isChildNode(getContentColumnName(row), rows)) {
            return new ValidationResult(false, "当层级类型为一级并且是否为检修项为是时，该节点不可以有子级");
        }

        return new ValidationResult(true, null);
    }

    /**
     * 判断父级层级是否为子级.
     *
     * @param data 当前节点的数据
     * @param rows 所有的行数据
     * @return 如果父级层级是子级则返回true，否则返回false
     */
    private boolean parentLevelIsSecondary(Object data, List<Map<String, Column>> rows) {
        return rows.stream()
                .filter(row -> Objects.equals(row.get("code"), data))
                .map(row -> row.get("has_child"))
                .filter(Objects::nonNull)
                .map(Column::getData)
                .anyMatch(InspectionConstant.LEVEL_TYPE_1::equals);
    }


    /**
     * 检查指定的行是否为一级项目
     *
     * @param row 行数据
     * @return 如果行为一级项目，则返回 true，否则返回 false
     */
    private boolean isFirstLevelWithProject(Map<String, Column> row) {
        return InspectionConstant.LEVEL_TYPE_0.equals(getData(row, "has_child")) && isPatrolProject(row);
    }

    /**
     * 检查指定的行是否为巡检项目
     *
     * @param row 行数据
     * @return 如果行为巡检项目，则返回 true，否则返回 false
     */
    private boolean isPatrolProject(Map<String, Column> row) {
        return InspectionConstant.SHI.equals(getData(row, "type"));
    }

    /**
     * 获取指定行的指定列数据
     *
     * @param row        行数据
     * @param columnName 列名
     * @return 列数据
     */
    private String getData(Map<String, Column> row, String columnName) {
        Column column = row.get(columnName);
        return column != null ? String.valueOf(column.getData()) : null;
    }

    /**
     * 获取指定行的内容列名
     *
     * @param row 行数据
     * @return 内容列名
     */
    private String getContentColumnName(Map<String, Column> row) {
        return getData(row, "code");
    }

    /**
     * 检查指定的内容列是否为其他行的子节点
     *
     * @param contentColumnData code值
     * @param rows              所有行数据
     * @return 如果内容列名为其他行的子节点，则返回 true，否则返回 false
     */
    private boolean isChildNode(String contentColumnData, List<Map<String, Column>> rows) {
        if (CollUtil.isEmpty(rows) || StrUtil.isEmpty(contentColumnData)) {
            return false;
        }
        for (Map<String, Column> row : rows) {
            if (isSecondLevelWithParent(row, contentColumnData)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查指定的行是否为二级项目且父节点名称与指定的名称相同
     *
     * @param row               行数据
     * @param contentColumnData code值
     * @return 如果行为二级项目且父节点名称与指定的名称相同，则返回 true，否则返回 false
     */
    private boolean isSecondLevelWithParent(Map<String, Column> row, String contentColumnData) {
        return InspectionConstant.LEVEL_TYPE_1.equals(getData(row, "has_child"))
                && StrUtil.isNotEmpty(contentColumnData)
                && contentColumnData.equals(getData(row, "pid"));
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
