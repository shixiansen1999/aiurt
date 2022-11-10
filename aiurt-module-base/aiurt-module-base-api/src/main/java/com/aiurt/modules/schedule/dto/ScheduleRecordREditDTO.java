package com.aiurt.modules.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(value = "调班对象", description = "调班对象")
public class ScheduleRecordREditDTO {

    @ApiModelProperty(value = "按天排班：0；按周期排班：1")
    private String schedulingMethod;

    @ApiModelProperty(value = "班次id")
    private  Integer  scheduleItemId;

    @ApiModelProperty(value = "规则id")
    private  Integer  scheduleRuleId;

    /**开始时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**结束时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "结束时间")
    private  Date  endTime;

    @ApiModelProperty(value = "排班人员id")
    private String userId;

    @ApiModelProperty(value = "排班记录id")
    private Integer scheduleRecordId;

}
