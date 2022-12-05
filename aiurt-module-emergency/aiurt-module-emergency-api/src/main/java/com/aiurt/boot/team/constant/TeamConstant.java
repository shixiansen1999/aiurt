package com.aiurt.boot.team.constant;
/**
 * @author
 * @date 2022/11/29 14:58
 * @description: 应急队伍模块通用常量类
 */
public interface TeamConstant {
    /**
     * 管理员身份
     */
    String ADMIN = "admin";
    /**
     * 未删除状态
     */
    Integer DEL_FLAG0 = 0;
    /**
     * 已删除状态
     */
    Integer DEL_FLAG1 = 1;

    /**
     * 添加方式：发布
     */
    Integer PUBLISH = 1;

    /**
     * 添加方式：保存
     */
    Integer SAVE = 0;
    /**
     * 审核状态:1待下发
     */
    Integer WAIT_PUBLISH = 1;
    /**
     * 审核状态:2待完成
     */
    Integer WAIT_COMPLETE= 2;
    /**
     * 审核状态:3已完成
     */
    Integer COMPLETED = 3;
}
