package com.aiurt.boot.statistics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 开始时间，格式yyyy-MM-dd
     */
    @ApiModelProperty(value = "开始时间，格式yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不能为空")
    private Date startDate;
    /**
     * 结束时间，格式yyyy-MM-dd
     */
    @ApiModelProperty(value = "结束时间，格式yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;
    /**
     * 站点code
     */
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    /**
     * 线路code
     */
    @ApiModelProperty(value = "线路code")
    private String lineCode;
    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    private Integer status;
    /**
     * 漏巡状态
     */
    @ApiModelProperty(value = "漏巡状态")
    private Integer omitStatus;
}
