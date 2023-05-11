package com.aiurt.modules.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class QWeatherDTO implements Serializable {


    @ApiModelProperty(value = "主键")
    private Long indocno;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "预报日期(yyyy-MM-dd)")
    private String fxDate;

    @ApiModelProperty(value = "预报日期(年月日)")
    private String fxDateCN;

    @ApiModelProperty(value = "预报日期(星期几)")
    private String fxDateOfWeek;

    @ApiModelProperty(value = "最高温度")
    private String tempMax;

    @ApiModelProperty(value = "最低温度")
    private String tempMin;

    @ApiModelProperty(value = "白天的天气描述")
    private String textDay;

    @ApiModelProperty(value = "夜晚的天气描述")
    private String textNight;

    @ApiModelProperty(value = "白天图标")
    private String iconDay;

    @ApiModelProperty(value = "夜晚图标")
    private String iconNight;
}
