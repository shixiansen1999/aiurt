package com.aiurt.config.datafilter.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限规则类型常量类。
 *
 * @author aiurt
 * @date 2022-07-18
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
     * 仅查看管理部门
     */
    public static final String TYPE_MANAGE_DEPT = "TYPE_MANAGE_DEPT";

    /**
     * 仅查看管理线路
     */
    public static final String TYPE_MANAGE_LINE_ONLY = "TYPE_MANAGE_LINE_ONLY";

    /**
     * 仅查看管理站点
     */
    public static final String TYPE_MANAGE_STATION_ONLY = "TYPE_MANAGE_STATION_ONLY";

    /**
     * 仅查看管理专业
     */
    public static final String TYPE_MANAGE_MAJOR_ONLY = "TYPE_MANAGE_MAJOR_ONLY";

    /**
     * 仅查看管理子系统
     */
    public static final String TYPE_MANAGE_SYSTEM_ONLY = "TYPE_MANAGE_SYSTEM_ONLY";

    private static final Map<Object, String> DICT_MAP = new HashMap<>(8);

    static {
        DICT_MAP.put(TYPE_USER_ONLY, "查看全部");
        DICT_MAP.put(TYPE_USER_ONLY, "仅查看当前用户");
        DICT_MAP.put(TYPE_DEPT_ONLY, "仅查看所在部门");
        DICT_MAP.put(TYPE_MANAGE_DEPT, "仅查看管理的部门");
        DICT_MAP.put(TYPE_MANAGE_LINE_ONLY, "仅查看管理线路");
        DICT_MAP.put(TYPE_MANAGE_STATION_ONLY, "仅查看管理站点");
        DICT_MAP.put(TYPE_MANAGE_MAJOR_ONLY, "仅查看管理专业");
        DICT_MAP.put(TYPE_MANAGE_SYSTEM_ONLY, "仅查看管理子系统");
    }

    /**
     * 判断参数是否为当前常量字典的合法取值范围。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(String value) {
        return value != null && DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DataPermRuleType() {
    }
}
