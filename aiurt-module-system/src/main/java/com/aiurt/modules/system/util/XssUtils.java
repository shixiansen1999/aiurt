package com.aiurt.modules.system.util;

import org.springframework.web.util.HtmlUtils;

import java.util.regex.Pattern;

/**
 * @Description: 工具类XSSUtils，现在的做法是替换成空字符，CSDN的是进行转义，比如文字开头的"<"转成&lt;
 * @author: lsq
 * @date: 2021年07月26日 19:13
 */
public class XssUtils {
    private static final Pattern PATTEN_1 = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTEN_2 =Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATTEN_3 = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATTEN_4 =Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTEN_5 =Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATTEN_6 =Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATTEN_7 =Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern PATTEN_8 =Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTEN_9 = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTEN_10 =Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    public static String striptXss(String value) {
        if (value != null) {
            value = value.replaceAll(" ", "");
            value = PATTEN_1.matcher(value).replaceAll("");
            value = PATTEN_2.matcher(value).replaceAll("");
            value = PATTEN_3.matcher(value).replaceAll("");
            value = PATTEN_4.matcher(value).replaceAll("");
            value = PATTEN_5.matcher(value).replaceAll("");
            value = PATTEN_6.matcher(value).replaceAll("");
            value = PATTEN_7.matcher(value).replaceAll("");
            value = PATTEN_8.matcher(value).replaceAll("");
            value = PATTEN_9.matcher(value).replaceAll("");
            value = PATTEN_10.matcher(value).replaceAll("");
        }
        return HtmlUtils.htmlEscape(value);
    }

    public static void main(String[] args) {
        String s = striptXss("<img  src=x onload=alert(111).*?><script></script>javascript:eval()\\\\.");
        System.err.println("s======>" + s);
    }
}
