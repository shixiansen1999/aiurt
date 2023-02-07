package com.aiurt.boot.materials.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "")
public class CheckResultDTO {

    /**
     * 巡检结果
     */
    @ApiModelProperty(value = "巡检结果")
    private Integer checkResult;

    private String writeValue;
}
