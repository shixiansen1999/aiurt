package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author
 * @description
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "任务次数", description = "任务次数")
public class WaveResDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private Integer year;

    /**
     * 巡视数
     */
    @ApiModelProperty(value = "巡视数")
    private BigDecimal patrol;

    /**
     * 检修数
     */
    @ApiModelProperty(value = "检修数")
    private BigDecimal inspection;

    /**
     * 故障数
     */
    @ApiModelProperty(value = "故障数")
    private BigDecimal fault;
}
