package com.aiurt.boot.statistics.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 首页巡检的站点DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexStationDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 站点编号
     */
    @ApiModelProperty(value = "站点编号")
    private java.lang.String stationCode;
    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private java.lang.String stationName;
}
