package com.aiurt.modules.worklog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalTime;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
public class WorkLogRemindDTO {

    @ApiModelProperty(value = "白班提醒时间")
    @JsonFormat(pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime remindTime;

    @ApiModelProperty(value = "夜班提醒时间")
    @JsonFormat(pattern = "HH:mm:ss")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime remindTimeNight;

    @ApiModelProperty(value = "提醒内容")
    @NotBlank(message = "提醒内容不能为空")
    private  String  content;
}
