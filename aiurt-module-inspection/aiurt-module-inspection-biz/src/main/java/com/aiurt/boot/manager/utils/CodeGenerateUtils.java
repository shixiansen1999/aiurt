package com.aiurt.boot.manager.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author wgp
 * @Title:
 * @Description: 检修单号生成工具类
 * @date 2022/7/516:04
 */
public class CodeGenerateUtils {

    /**
     * 生成单号
     *
     * @param prefix 前缀
     * @return
     */
    public static String generateCode(String prefix) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String jxCode = String.format("%s%s", prefix, snowflake.nextIdStr());
        return jxCode;
    }

}
