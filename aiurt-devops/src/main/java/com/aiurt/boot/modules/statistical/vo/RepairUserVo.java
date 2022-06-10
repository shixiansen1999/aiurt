package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RepairUserVo {
    @ApiModelProperty(value = "班组")
    private String teamName;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "总检修数")
    private Integer repairAmount;
    @ApiModelProperty(value = "总巡修数")
    private Integer patrolAmount;
    @ApiModelProperty(value = "故障处理数")
    private Integer faultHandleAmount;
}
