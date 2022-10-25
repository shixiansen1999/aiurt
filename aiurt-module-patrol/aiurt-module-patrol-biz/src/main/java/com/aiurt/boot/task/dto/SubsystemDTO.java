package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cgkj0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "子系统联动对象", description = "子系统联动对象")
public class SubsystemDTO {
    /**
     * 子系统编码
     */
    @ApiModelProperty(value = "子系统编码")
    private java.lang.String subsystemCode;
    /**
     * 子系统名称
     */
    @ApiModelProperty(value = "子系统名称")
    private java.lang.String subsystemName;
}
