package com.aiurt.modules.maplocation.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/11/516:41
 */
@Data
public class StationDTO {

    @ApiModelProperty("站点id")
    private String stationId; // 站点id
    @ApiModelProperty(value = "对应站点的位置X")
    private String positionX; // 对应站点的位置X
    @ApiModelProperty(value = "对应站点的位置Y")
    private String positionY; // 对应站点的位置Y
    @ApiModelProperty(value = "站点名称")
    private String stationName;
}
