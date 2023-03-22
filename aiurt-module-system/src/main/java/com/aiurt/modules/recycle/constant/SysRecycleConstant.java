package com.aiurt.modules.recycle.constant;

/**
 * 回收站常用常量
 */
public interface SysRecycleConstant {
    /**是否逻辑删除, 1是 0否 */
    public static final Integer DEL_SIGN_1 = 1;
    /**是否逻辑删除, 1是 0否 */
    public static final Integer DEL_SIGN_0 = 0;
    /**状态（（1正常2还原3删除））*/
    public static final Integer STATE_NORMAL = 1;
    /**状态（（1正常2还原3删除））*/
    public static final Integer STATE_RESTORE = 2;
    /**状态（（1正常2还原3删除））*/
    public static final Integer STATE_DELETE = 3;
}
