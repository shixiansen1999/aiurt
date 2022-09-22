package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-15 14:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultSystemReliabilityDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "可靠度")
    private String  reliability;

    @ApiModelProperty(value = "计划运行时间")
    private Integer  scheduledRuntime;

    @ApiModelProperty(value = "实际运行时间")
    private Integer actualRuntime;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty(value = "专业子系统名称")
    private String systemName;



}
