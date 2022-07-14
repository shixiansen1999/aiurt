package com.aiurt.boot.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 巡检模块编号生成工具类
 *
 * @author CJB
 */
public class PatrolCodeUtil {

    /**
     * 获取巡检标准编号
     *
     * @return
     */
    public static String getStandardCode() {
        String code = getCommonCode("XB");
        return code;
    }

    /**
     * 获取巡检计划编号
     *
     * @return
     */
    public static String getPlanCode() {
        String code = getCommonCode("XJ");
        return code;
    }

    /**
     * 获取巡检任务编号
     *
     * @return
     */
    public static String getTaskCode() {
        String code = getCommonCode("XR");
        return code;
    }

    /**
     * 获取巡检单编号
     *
     * @return
     */
    public static String getBillCode() {
        String code = getCommonCode("XD");
        return code;
    }

    /**
     * 自定义前缀获取编号
     *
     * @param prefix
     * @return
     */
    public static String getCommonCode(String prefix) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String patrolCode = String.format("%s%s", prefix, snowflake.nextIdStr());
        return patrolCode;
    }
}
