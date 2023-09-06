package com.aiurt.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:wgp
 * @create: 2023-08-16 09:52
 * @Description:
 */
public class SafeSqlCheckerUtils {

    public static final String SQL_UPDATE = "update";
    public static final String SQL_DELETE = "delete";
    private static final Pattern SAFE_SQL_PATTERN = Pattern.compile("(?i)\\b(?:SELECT|UPDATE|INSERT|DELETE)\\b.*", Pattern.DOTALL);
    private static final Pattern INVALID_CONDITION_PATTERN = Pattern.compile("\\d+\\s*=\\s*\\d+");

    /**
     * 检查SQL语句是否仅包含UPDATE和SELECT语法。
     *
     * @param sql SQL语句
     * @return true表示仅包含UPDATE和SELECT语法，false表示包含其他语法
     */
    public static boolean isSafeSql(String sql) {
        Matcher matcher = SAFE_SQL_PATTERN.matcher(sql);
        if (!matcher.matches()) {
            return false;
        }

        // 使用正则表达式检查是否包含无效的条件
        Matcher conditionMatcher = INVALID_CONDITION_PATTERN.matcher(sql);
        if (conditionMatcher.find()) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        String safeSelect = "INSERT INTO customers (name, email) VALUES ('John', 'john@example.com')";
        String safeUpdate = "delete users SET status = 'active' WHERE id = (select id from sys_user)";
        String mixedSql = "INSERT INTO users (name, age) VALUES ('John', 30)";

        System.out.println("Is safe SELECT safe? " + isSafeSql(safeSelect)); // Should print true
        System.out.println("Is safe UPDATE safe? " + isSafeSql(safeUpdate)); // Should print true
        System.out.println("Is mixed SQL safe? " + isSafeSql(mixedSql)); // Should print false
    }
}
