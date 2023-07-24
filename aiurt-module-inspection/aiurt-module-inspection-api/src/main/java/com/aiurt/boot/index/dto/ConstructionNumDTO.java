package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConstructionNumDTO {
    @ApiModelProperty("作业日期")
    private String taskDate;
    @ApiModelProperty("作业数")
    private Integer num;
}
