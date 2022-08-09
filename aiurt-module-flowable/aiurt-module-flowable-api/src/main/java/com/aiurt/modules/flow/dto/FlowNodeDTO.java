package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:流程节点的DTO
 * @date 2022/8/810:45
 */
@Data
public class FlowNodeDTO {
    /**
     * 节点id
     */
    @ApiModelProperty("节点id")
    private String nodeId;
    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;
    /**
     * 执行人真实姓名
     */
    @ApiModelProperty("执行人真实姓名")
    private String realName;
    /**
     * 执行人账号
     */
    @ApiModelProperty("执行人账号")
    private String userName;

    /**
     * 任务节点结束时间
     */
    @ApiModelProperty("任务节点结束时间")
    private Date endTime;
}
