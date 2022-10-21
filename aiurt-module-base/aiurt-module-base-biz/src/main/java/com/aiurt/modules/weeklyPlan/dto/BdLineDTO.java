package com.aiurt.modules.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdLineDTO {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "线路名称")
    private String name;

    @ApiModelProperty(value = "线路编码")
    private String code;
}
