package com.aiurt.modules.modeler.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class CompareDTO implements Serializable {

    @ApiModelProperty(value = "模型id")
    private String modelId;

    @ApiModelProperty(value = "xml")
    private String modelXml;
}
