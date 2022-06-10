package com.aiurt.boot.modules.message.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author fanghaifeng
 * @Date: 2019/12/23
 * @Description:
 */
@Data
@Builder
public class TemplateMsg {

    /**
     * 标题
     */
    private String title;

    private String fromUser;

    /**
     * 接收用户id
     */
    private String userIds;
    /**
     * 申请部门
     */
    private String deaprtName;
    /**
     * 时间
     */
    private Date msgDate;

    private String extend;

    private String detailUrl;
}
