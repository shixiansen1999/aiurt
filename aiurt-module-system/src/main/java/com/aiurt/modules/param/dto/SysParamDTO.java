package com.aiurt.modules.param.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统参数条件分页列表DTO对象
 */
@ApiModel(value = "系统参数条件分页列表DTO对象", description = "系统参数条件分页列表DTO对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysParamDTO {
    /**
     * 参数编号
     */
    @ApiModelProperty(value = "参数编号")
    private java.lang.String code;
    /**
     * 参数类别
     */
    @ApiModelProperty(value = "参数类别")
    private java.lang.Integer category;
    /**
     * 参数值
     */
    @ApiModelProperty(value = "参数值")
    private java.lang.String value;
    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    private java.lang.String explain;

}
