package com.aiurt.modules.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 * @date 2022-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDetailDTO implements Serializable {

    private static final long serialVersionUID = -4603664431778639969L;

    @ApiModelProperty(value = "城市名")
    private String cityName;

    @ApiModelProperty(value = "城市编码")
    private String cityKey;

    @ApiModelProperty("更新时间")
    private String updateTime;

    @ApiModelProperty(value = "湿度")
    private String humidity;

    private String pm25;

    private String pm10;

    private String quality;

    private String type;
}
