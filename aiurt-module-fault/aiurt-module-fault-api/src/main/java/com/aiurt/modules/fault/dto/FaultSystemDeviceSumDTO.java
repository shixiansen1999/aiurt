package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-15 14:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultSystemDeviceSumDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String systemCode;

    @ApiModelProperty(value = "专业子系统名称")
    private String systemName;

    @ApiModelProperty(value = "线路编码")
    private String lineCode;

    @ApiModelProperty(value = "线路名称")
    private String lineName;

    @ApiModelProperty(value = "工作时长")
    private String shouldWorkTime;

    @ApiModelProperty(value = "子系统设备数")
    private Integer deviceNumber;


}
