package com.aiurt.boot.team.constants;
/**
 * @author
 * @date 2022/11/29 14:58
 * @description: 应急队伍模块通用常量类
 */
public class TeamConstant {
    /**
     * 未删除状态
     */
    public static final Integer DEL_FLAG0 = 0;
    /**
     * 已删除状态
     */
    public static final Integer DEL_FLAG1 = 1;

    /**
     * 添加方式：发布
     */
    public static final Integer PUBLISH = 1;

    /**
     * 添加方式：保存
     */
    public static final Integer SAVE = 0;
    /**
     * 审核状态:1待下发
     */
    public static final Integer WAIT_PUBLISH = 1;
    /**
     * 审核状态:2待完成
     */
    public static final Integer WAIT_COMPLETE= 2;
    /**
     * 审核状态:3已完成
     */
    public static final Integer COMPLETED = 3;

    /**
     * 提交状态:1待提交
     */
    public static final Integer To_BE_SUBMITTED = 1;

    /**
     * 提交状态:2已提交
     */
    public static final Integer SUBMITTED = 2;

    /**
     *职务字典值编码
     */
    public static final String EMERGENCY_POST = "emergency_post";
    /**
     *岗位字典值编码
     */
    public static final String SYS_POST = "sys_post";

}
