package com.aiurt.config.datafilter.utils;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;

import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-04-27 09:00
 * @Description:
 */
@Slf4j
public class SqlBuilderUtil {

    /**
     * 根据给定的列名和规则值，构建一个使用 IN 操作符的 SQL 过滤条件。
     *
     * @param columnName 列名，对应于数据库表中的字段名
     * @param ruleValue  规则值，用于构建 IN 操作符的值列表
     * @return 返回一个包含 IN 操作符的 SQL 过滤条件字符串，形式为 "columnName IN (ruleValue)"
     * @throws IllegalArgumentException 如果 columnName 或 ruleValue 为空或者无效
     */
    private static String buildCondition(String columnName, String ruleValue) {
        if (columnName == null || columnName.isEmpty() || ruleValue == null || ruleValue.isEmpty()) {
            throw new IllegalArgumentException("列名和规则值不能为空");
        }
        return columnName + " IN (" + ruleValue + ")";
    }

    /**
     * 根据给定的规则映射和列映射，构建一个包含数据过滤条件的 SQL 查询语句。
     *
     * @param dataRules     一个映射，其中 key 是规则标志，value 是规则值
     * @param columnMapping 一个映射，其中 key 是规则标志，value 是数据库表中对应的列名
     * @return 返回一个包含数据过滤条件的 SQL 查询语句
     * @throws IllegalArgumentException 如果 dataRules 或 columnMapping 为空或者无效
     */
    public static String buildSql(Map<String, String> dataRules, Map<String, String> columnMapping) {
        if (dataRules == null || columnMapping == null) {
            return null;
        }

        // 通常对于无需登录的白名单url，也无需过滤了。
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(sysUser)) {
            return null;
        }

        // 默认对管理员角色不进行数据过滤
        if (sysUser.getRoleCodes().contains(CommonConstant.ADMIN)) {
            return null;
        }

        StringBuilder sql = new StringBuilder();

        int i = 0;
        for (Map.Entry<String, String> entry : dataRules.entrySet()) {
            String ruleFlag = entry.getKey();
            String ruleValue = entry.getValue();

            // 通过规则标志找到对应的数据库字段
            String columnName = columnMapping.get(ruleFlag);

            if (columnName != null) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(buildCondition(columnName, ruleValue));
                i++;
            } else {
                // 处理规则标志在 columnMapping 中找不到对应字段的情况（可选）
                log.error("无法找到规则标志{}对应的数据库字段", ruleFlag);
            }
        }

        return sql.toString();
    }

}
