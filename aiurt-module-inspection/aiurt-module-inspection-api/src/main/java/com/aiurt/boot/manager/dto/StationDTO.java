package com.aiurt.boot.manager.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 位置信息
 * @date 2022/6/2810:25
 */
@Data
public class StationDTO {
    /**站所编号*/
    @ApiModelProperty(value = "站所编号")
    private java.lang.String stationCode;
    /**线路编号*/
    @ApiModelProperty(value = "线路编号")
    private java.lang.String lineCode;
    /**位置编号*/
    @ApiModelProperty(value = "位置编号")
    private java.lang.String positionCode;
}
