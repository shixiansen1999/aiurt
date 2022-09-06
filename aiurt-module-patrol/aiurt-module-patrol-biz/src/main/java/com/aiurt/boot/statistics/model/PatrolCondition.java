package com.aiurt.boot.statistics.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolCondition implements Serializable {
    private static final long serialVersionUID = 1L;
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
