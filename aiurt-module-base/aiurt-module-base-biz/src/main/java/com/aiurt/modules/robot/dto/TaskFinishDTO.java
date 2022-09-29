package com.aiurt.modules.robot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author JB
 * @Description: 机器人巡检任务查询DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "TaskFinishDTO", description = "机器人巡检任务查询DTO")
public class TaskFinishDTO {
    /**
     * 线路编号
     */
    @ApiModelProperty(value = "线路编号")
    private String lineCode;
    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    private String taskStatus;
    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
