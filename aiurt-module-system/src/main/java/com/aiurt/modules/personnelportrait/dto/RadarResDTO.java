package com.aiurt.modules.personnelportrait.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
     * 故障处理总次数
     */
    @ApiModelProperty(value = "故障处理总次数")
    private Double handle;

    /**
     * 绩效
     */
    @ApiModelProperty(value = "绩效")
    private Double performance;

    /**
     * 资质
     */
    @ApiModelProperty(value = "资质")
    private Double aptitude;

    /**
     * 工龄
     */
    @ApiModelProperty(value = "工龄")
    private Double seniority;

    /**
     * 解决效率
     */
    @ApiModelProperty(value = "解决效率")
    private Double efficiency;
}
