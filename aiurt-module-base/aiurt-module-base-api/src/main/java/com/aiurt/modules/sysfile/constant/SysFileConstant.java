package com.aiurt.modules.sysfile.constant;

/**
 * @author:wgp
 * @create: 2023-05-22 18:29
 * @Description:
 */
public class SysFileConstant {

    /**
     * 1代表有权限
     */
    public static final Boolean WITH_PERMISSION_1 = true;

    /**
     * 定义常量，表示字节与千字节、兆字节之间的转换关系。
     * 1KB = 1024字节
     * 1MB = 1048576字节
     */
    public static final int BYTES_IN_KB = 1024;
    public static final int BYTES_IN_MB = 1048576;
    /**
     * 顶层父级
     */
    public static final Long NUM_LONG_0 = 0L;

    /**
     * 允许查看权限
     */
    public static final Integer PERMISSION_VIEW = 1;

    /**
     * 允许下载权限
     */
    public static final Integer PERMISSION_DOWNLOAD = 2;

    /**
     * 允许在线编辑权限
     */
    public static final Integer PERMISSION_EDIT_ONLINE = 3;

    /**
     * 允许删除权限
     */
    public static final Integer PERMISSION_DELETE = 4;

    /**
     * 允许编辑权限
     */
    public static final Integer PERMISSION_EDIT = 5;

    /**
     * 可管理权限
     */
    public static final Integer PERMISSION_MANAGE = 6;
}
