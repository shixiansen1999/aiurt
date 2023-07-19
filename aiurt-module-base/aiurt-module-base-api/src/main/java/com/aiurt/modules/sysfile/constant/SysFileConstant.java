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
     * 定义常量，表示字节与千字节、兆字节、千兆字节之间的转换关系。
     * 1KB = 1024字节
     * 1MB = 1048576字节
     * 1GB = 1073741824字节
     */
    public static final int BYTES_IN_KB = 1024;
    public static final int BYTES_IN_MB = 1048576;
    public static final int BYTES_IN_GB = 1073741824;
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

    /**
     * 数据字典，代表下载状态
     */
    public static final String DOWNLOAD_STATUS = "download_status";

    /**
     * 下载记录导出的字段
     */
    public static final String DOWNLOAD_RECORD_EXPORT_FIELD = "userName,downloadTime,fileName,size,downloadStatusName,downloadDuration";

    /**
     * 字节单位：B
     */
    public static final String B = "B";

    /**
     * 千字节单位：KB
     */
    public static final String KB = "KB";

    /**
     * 兆字节单位：MB
     */
    public static final String MB = "MB";
    /**
     * 千兆字节单位：GB
     */
    public static final String GB = "GB";
}
