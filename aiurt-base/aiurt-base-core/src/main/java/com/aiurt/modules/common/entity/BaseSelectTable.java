package com.aiurt.modules.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desc 普通树形结构
 * @author fgw
 */
@Data
@ApiModel("下拉树")
public class BaseSelectTable {

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "label")
    private String label;
}
