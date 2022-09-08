package com.aiurt.modules.basic.dto;

import io.swagger.annotations.Api;
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

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市名")
    private String city;

    @ApiModelProperty(value = "区域编码")
    private String adcode;

    @ApiModelProperty(value = "天气现象（汉字描述）")
    private String weather;

    @ApiModelProperty(value = "实时气温，单位：摄氏度")
    private String temperature;

    @ApiModelProperty(value = "风向描述")
    private String winddirection;

    @ApiModelProperty(value = "风力级别，单位：级")
    private String  windpower;

    @ApiModelProperty(value = "空气湿度")
    private String humidity;

    @ApiModelProperty(value = "数据发布的时间")
    private String reporttime;
}
