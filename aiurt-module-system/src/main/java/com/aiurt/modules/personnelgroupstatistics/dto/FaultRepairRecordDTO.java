package com.aiurt.modules.personnelgroupstatistics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author lkj
 * @Title:
 * @Description:
 * @date 2022/10/11 10:56
 */
@Data
public class FaultRepairRecordDTO {

    @ApiModelProperty("维修记录id")
    private String id;

    @ApiModelProperty(value = "维修负责人")
    private String appointUserName;

    @ApiModelProperty(value = "维修负责人名称")
    private String appointRealName;

    /**接收任务时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接收任务时间")
    private Date receviceTime;

    /**开始维修时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始维修时间")
    private Date startTime;

    /**维修完成时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

    /**维修响应时长*/
    @Excel(name = "维修响应时长", width = 15)
    @ApiModelProperty(value = "维修响应时长")
    private Integer responseDuration;
}
