package com.aiurt.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * @author : sbx
 * @Classname : ArchExecelUtil
 * @Description : 归档excel处理工具类
 * @Date : 2023/9/8 12:10
 */
public class ArchExecelUtil {
    public static final String FONT_ST = "宋体";
    /**
     * 字母或字符
     */
    public static Pattern p1 = Pattern.compile("^[A-Za-z0-9]+$");
    /**
     * 全角
     */
    public static Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]+$");
    /**
     * 全角符号 及中文
     */
    public static Pattern p3 = Pattern.compile("[^x00-xff]");

    /**
     * str 是单元格需要放入的 字符串 fontCountInline 是该单元格每行多少个汉字 全角为1 英文或符号为0.5
     * @param str
     * @param fontCountInline
     * @return
     */
    public static float getExcelCellAutoHeight(String str, float fontCountInline) {
        //每一行的高度指定
        float defaultRowHeight = 25.00f;
        float defaultCount = 0.00f;
        for (int i = 0; i < str.length(); i++) {
            float ff = getregex(str.substring(i, i + 1));
            defaultCount = defaultCount + ff;
        }
        //计算
        return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;
    }

    public static float getregex(String charStr) {

        if(StrUtil.SPACE.equals(charStr))
        {
            return 0.5f;
        }
        // 判断是否为字母或字符
        if (p1.matcher(charStr).matches()) {
            return 0.5f;
        }
        // 判断是否为全角
        if (p2.matcher(charStr).matches()) {
            return 1.00f;
        }
        //全角符号 及中文
        if (p3.matcher(charStr).matches()) {
            return 1.00f;
        }
        return 0.5f;
    }
}
