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
@ApiModel(value = "人员综合表现", description = "人员综合表现")
public class RadarResDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 故障处理总次数
     */
    @ApiModelProperty(value = "故障处理总次数")
    private BigDecimal handle;

    /**
     * 绩效
     */
    @ApiModelProperty(value = "绩效")
    private BigDecimal performance;

    /**
     * 资质
     */
    @ApiModelProperty(value = "资质")
    private BigDecimal aptitude;

    /**
     * 工龄
     */
    @ApiModelProperty(value = "工龄")
    private BigDecimal seniority;

    /**
     * 解决效率
     */
    @ApiModelProperty(value = "解决效率")
    private BigDecimal efficiency;
}
