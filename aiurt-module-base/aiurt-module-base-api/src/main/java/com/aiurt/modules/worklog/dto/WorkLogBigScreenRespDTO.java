package com.aiurt.modules.worklog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 大屏-工作日志响应DTO
 *
 * @author 华宜威
 * @date 2023-07-10 11:54:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkLogBigScreenRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工作日志id")
    private String id;

    @ApiModelProperty(value = "班组id")
    private String orgId;
    @ApiModelProperty(value = "班组名称")
    private String orgName;

    /**提交时间*/
    @Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "提交状态:0-未提交 1-已提交")
    private Integer submitStatus;

    @ApiModelProperty(value = "确认状态:0-未确认 1-已确认")
    private Integer confirmStatus;

    /**工作日志状态，待提交，待确认，已完成。注：待确认就是已提交未确认*/
    @ApiModelProperty(value = "工作日志状态，待提交，待确认，已完成")
    private String stateName;
}
