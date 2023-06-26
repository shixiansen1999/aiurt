package com.aiurt.modules.worklog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 首页-工作日志 列表展示的DTO
 * @author 华宜威
 * @date 2023-06-26 10:01:46
 */
@Data
public class WorkLogIndexShowDTO implements Serializable {
    /**班组名称*/
    @ApiModelProperty(value = "班组名称")
    private String orgName;
    /**班组Id*/
    @ApiModelProperty(value = "班组Id")
    private String orgId;
    /**工作日志编号*/
    @ApiModelProperty(value = "工作日志编号")
    private String code;
    /**提交时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;
    /**确认状态:0-未确认 1-已确认*/
    @ApiModelProperty(value = "确认状态:0-未确认 1-已确认")
    private Integer confirmStatus;
    /**确认状态翻译*/
    @ApiModelProperty(value = "确认状态翻译")
    private String confirmStatusName;
}
