package com.aiurt.config.mybatis.constant;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限规则类型常量类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class DataPermRuleType {

    /**
     * 查看全部。
     */
    public static final String TYPE_ALL = "TYPE_ALL";

    /**
     * 仅查看当前用户
     */
    public static final String TYPE_USER_ONLY = "TYPE_USER_ONLY";

    /**
     * 仅查看当前部门
     */
    public static final String TYPE_DEPT_ONLY = "TYPE_DEPT_ONLY";

    /**
     * 所管理的部门
     */
    public static final String TYPE_DEPT_MANAGED = "TYPE_DEPT_MANAGED";

    /**
     * 所管理的线路
     */
    public static final String TYPE_LINE_MANAGED = "TYPE_LINE_MANAGED";

    /**
     * 所管理的站点
     */
    public static final String TYPE_STATION_MANAGED = "TYPE_STATION_MANAGED";

    /**
     * 所管理的专业
     */
    public static final String TYPE_MAJOR_MANAGED = "TYPE_MAJOR_MANAGED";

    /**
     * 所管理的专业子系统
     */
    public static final String TYPE_SUBSYSTEM_MANAGED = "TYPE_SUBSYSTEM_MANAGED";

    /**
     * 多部门及子部门
     */
    public static final String TYPE_MULTI_DEPT_AND_CHILD_DEPT = "TYPE_MULTI_DEPT_AND_CHILD_DEPT";

    /**
     * 自定义部门列表
     */
    public static final String TYPE_CUSTOM_DEPT_LIST = "TYPE_CUSTOM_DEPT_LIST";

    private static final Map<String, String> DICT_MAP = new HashMap<>(6);
    static {
        DICT_MAP.put(TYPE_ALL, "查看全部");
        DICT_MAP.put(TYPE_USER_ONLY, "仅查看当前用户");
        DICT_MAP.put(TYPE_DEPT_ONLY, "仅查看所在部门");
        DICT_MAP.put(TYPE_DEPT_MANAGED, "管理的部门");
        DICT_MAP.put(TYPE_MULTI_DEPT_AND_CHILD_DEPT, "多部门及子部门");
        DICT_MAP.put(TYPE_CUSTOM_DEPT_LIST, "自定义部门列表");
    }

    /**
     * 判断参数是否为当前常量字典的合法取值范围。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(String value) {
        return StrUtil.isNotEmpty(value) && DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DataPermRuleType() {
    }
}
