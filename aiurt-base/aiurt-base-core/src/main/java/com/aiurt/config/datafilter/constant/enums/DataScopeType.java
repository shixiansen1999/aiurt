package com.aiurt.config.datafilter.constant.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 * <p>
 * 语法支持 spel 模板表达式
 * <p>
 * 内置服务 sdss 系统数据权限服务 内容参考 sysBaseApiImpl
 * 如需扩展更多自定义服务 可以参考 sysBaseApiImpl 自行编写
 *
 * @author Lion Li
 * @version 3.5.0
 */
@Getter
@AllArgsConstructor
public enum DataScopeType {

    /**
     * 全部数据权限
     */
    TYPE_ALL("TYPE_ALL", "", ""),

    /**
     * 查看管理专业
     */
    TYPE_MANAGE_MAJOR_ONLY("TYPE_MANAGE_MAJOR_ONLY", " #{#majorName} IN ( #{@sdss.getMajorByUserIdStr( #user.id )} ) ", " 1 = 0"),

    /**
     * 查看管理子系统
     */
    TYPE_MANAGE_SYSTEM_ONLY("TYPE_MANAGE_SYSTEM_ONLY", " #{#systemName} IN ( #{@sdss.getSubsystemByUserIdStr( #sysUser.id )} )", " 1 = 0"),

    /**
     * 查看管理部门
     */
    TYPE_MANAGE_DEPT("TYPE_MANAGE_DEPT", " #{#deptName} IN ( #{@sdss.getDepartByUserIdStr( #sysUser.id )})", " 1 = 0"),

    /**
     * 查看当前部门
     */
    TYPE_DEPT_ONLY("TYPE_DEPT_ONLY", " #{#deptName} = #{#sysUser.orgCode} ", " 1 = 0 "),

    /**
     * 查看管理线路
     */
    TYPE_MANAGE_STATION_ONLY("TYPE_MANAGE_STATION_ONLY", "", " 1 = 0 "),

    /**
     * 仅查看管理站点
     */
    TYPE_USER_ONLY("TYPE_USER_ONLY", " #{#stationName} IN ( #{@sdss.getStationByUserIdStr( #sysUser.id )}) ", " 1 = 0 "),

    /**
     * 仅查看当前用户
     */
    TYPE_MANAGE_LINE_ONLY("TYPE_MANAGE_LINE_ONLY", " #{#userName} = #{#sysUser.id} ", " 1 = 0 ");

    private final String code;

    /**
     * 语法 采用 spel 模板表达式
     */
    private final String sqlTemplate;

    /**
     * 不满足 sqlTemplate 则填充
     */
    private final String elseSql;

    public static DataScopeType findCode(String code) {
        if (StrUtil.isBlank(code)) {
            return null;
        }
        for (DataScopeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
