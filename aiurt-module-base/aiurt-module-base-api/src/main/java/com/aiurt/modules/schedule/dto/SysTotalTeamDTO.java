package com.aiurt.modules.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysTotalTeamDTO {

    @ApiModelProperty(value = "班组名称")
    private String teamName;

    @ApiModelProperty(value = "班组组长")
    private String teamLeader;

    @ApiModelProperty(value = "工区位置")
    private String location;

    @ApiModelProperty(value = "站点个数")
    private Long number;
}
