package com.aiurt.modules.workticket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("工作票查询参数")
public class WorkTicketReqDTO implements Serializable {

    @ApiModelProperty(value = "类型")
    private String processName;

    @ApiModelProperty(value = "pageNo")
    private Integer pageNo = 0;

    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "变电所名", notes = "")
    private String stationName;

    @ApiModelProperty(value = "工区", notes = "1: 全部工区, 0: 本工区")
    private String siteType;

    @ApiModelProperty("工作票号")
    private String workTicketCode;

    @ApiModelProperty("填票人")
    private String ticketFiller;

    @ApiModelProperty("状态")
    private Integer state;

    @ApiModelProperty("内容")
    private String workAddressContent;

    @ApiModelProperty("负责人")
    private String workLeader;

    @ApiModelProperty("签发人")
    private String signeUser;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @ApiModelProperty(value = "填票时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @ApiModelProperty(value = "0:填票人, 1:负责人, 2:签发人 ")
    private String workTicketUser;

    private List<String> workFillerList;

}
