package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class StaffDataVo {

    @ApiModelProperty(value = "班组名称")
    private String orgName;
    @ApiModelProperty(value = "维修人员id")
    private String staffId;
    @ApiModelProperty(value = "维修人员姓名")
    private String staffName;
    @ApiModelProperty(value = "检修数")
    private Integer num1;
    @ApiModelProperty(value = "巡检数")
    private Integer num2;
    @ApiModelProperty(value = "故障处理数")
    private Integer num3;
}
