package com.aiurt.config.mybatis;

/**
 * @author wgp
 * @Title:
 * @Description: （权限过滤基础抽象类）
 * @date 2022/5/2315:49
 */
public abstract class PermissionFilter {
    /**
     * 需要追加的sql
     * @return
     */
    public abstract String getSql();


    public  String checkChar() {
        return null;
    }
}
