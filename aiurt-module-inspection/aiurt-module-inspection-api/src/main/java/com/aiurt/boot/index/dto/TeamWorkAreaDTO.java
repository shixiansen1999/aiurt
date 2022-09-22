package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeamWorkAreaDTO {
    @ApiModelProperty("班组id")
    private String teamId;
    @ApiModelProperty("班组code")
    private String teamCode;
    @ApiModelProperty("站点code")
    private String stationCode;
    @ApiModelProperty("站点name")
    private String stationName;
    @ApiModelProperty("线路code")
    private String lineCode;
    @ApiModelProperty("线路name")
    private String lineName;
    @ApiModelProperty("排序")
    private Integer sort;
}
