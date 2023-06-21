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
@ApiModel(value = "绩效", description = "绩效")
public class PerformanceResDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private Integer year;
    /**
     * 绩效分值
     */
    @ApiModelProperty(value = "绩效分值")
    private BigDecimal value;

}
