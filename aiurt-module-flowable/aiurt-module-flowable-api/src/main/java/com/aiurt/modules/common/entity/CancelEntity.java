package com.aiurt.modules.common.entity;

import java.io.Serializable;

/**
 * 终止，删除，撤销流程
 * @author fgw
 */
public class CancelEntity implements Serializable {

    private static final long serialVersionUID = -2777893345511447864L;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 操作用户账号
     */
    private String username;

    /**
     * 驳回理由
     */
    private String reason;

    /**
     * 主键id
     */
    private String id;

    /**
     * 流程标识
     */
    private String modelKey;
}
