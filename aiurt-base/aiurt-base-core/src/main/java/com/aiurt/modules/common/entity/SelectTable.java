package com.aiurt.modules.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("下列列表")
public class SelectTable {


    private String value;

    private String label;

    @ApiModelProperty("子级")
    private List<SelectTable> children;
}
