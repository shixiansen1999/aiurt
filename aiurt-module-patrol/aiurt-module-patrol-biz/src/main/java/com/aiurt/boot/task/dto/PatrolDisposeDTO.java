package com.aiurt.boot.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author cgkj0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "任务处置信息", description = "任务处置信息")
public class PatrolDisposeDTO {

    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    private String taskCode;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**
     * 漏检日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "漏检日期")
    private Date omitDate;
}
