package com.aiurt.modules.workticket.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("工作票查询结果")
public class WorkTicketResDTO implements Serializable {

    @ApiModelProperty("主键")
    private String id;

    @ApiModelProperty("流程实例")
    private String proInstanceId;

    @ApiModelProperty("流程任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("站点名称")
    private String stationName;

    @ApiModelProperty("工作票号")
    private String workTicketCode;

    @ApiModelProperty("票据类型")
    private String processName;

    @ApiModelProperty("填票人")
    private String ticketFiller;

    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("作业地点及内容")
    private String workAddressContent;

    @ApiModelProperty("填票时间")
    private String  createTime;

    @ApiModelProperty("驳回原因")
    private String  rejectionReason;


}
