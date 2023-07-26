package com.aiurt.modules.worklog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页-工作日志-未提交日志的响应DTO
 * @author 华宜威
 * @date 2023-07-25 16:01:17
 */
@Data
public class WorkLogIndexUnSubmitRespDTO implements Serializable {

    /**组织机构id*/
    @ApiModelProperty(value = "组织机构id")
    private String orgId;
    /**组织机构名称*/
    @ApiModelProperty(value = "组织机构名称")
    private String orgName;
    /**日志应该提交时间*/
    @ApiModelProperty(value = "日志应该提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date logTime;
    /**未提交日志数*/
    @ApiModelProperty(value = "未提交日志数")
    private Integer unSubmitNum;
    /**是否到期*/
    @ApiModelProperty(value = "是否到期")
    private Boolean deadLineFlag;
}
