package com.aiurt.boot.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  Lai W.
 * @version  1.0
 */

@Data
public class BdConstructionTypeDTO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "类型名称")
    private String name;

}
