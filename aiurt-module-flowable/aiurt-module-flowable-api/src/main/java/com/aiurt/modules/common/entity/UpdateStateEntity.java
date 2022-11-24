package com.aiurt.modules.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 *
 */
@Data
public class UpdateStateEntity implements Serializable {

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
    private String businessKey;

    /**
     * 流程标识
     */
    private String modelKey;

    /**
     * 状态
     */
    private Integer states;

}
