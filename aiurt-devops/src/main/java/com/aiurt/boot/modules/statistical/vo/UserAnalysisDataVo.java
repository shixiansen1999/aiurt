package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserAnalysisDataVo {
    @ApiModelProperty(value = "总人数")
    private Integer num1;
    @ApiModelProperty(value = "今日值班人数")
    private Integer num2;
    @ApiModelProperty(value = "班组数量")
    private Integer num3;

}
