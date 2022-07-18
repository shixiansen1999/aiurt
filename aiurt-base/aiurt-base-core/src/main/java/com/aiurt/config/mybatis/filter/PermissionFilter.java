package com.aiurt.config.mybatis.filter;

/**
 * @author wgp
 * @Title:
 * @Description: （权限过滤基础抽象类）
 * @date 2022/5/2315:49
 */
public abstract class PermissionFilter {
    /**
     * 需要追加的sql
     *
     * @return
     */
    public abstract String getSql();

    /**
     * 部门表的别名
     */
    public String deptAlias;

    /**
     * 用户表的别名
     */
    public String userAlias;

    /**
     * 线路表的别名
     */
    public String lineAlias;

    /**
     * 站点的别名
     */
    public String stationAlias;

    /**
     * 专业表的别名
     */
    public String majorAlias;

    /**
     * 站点的别名
     */
    public String subsystemAlias;

    public String checkChar() {
        return null;
    }
}
