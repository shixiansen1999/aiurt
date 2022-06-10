package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeviceDataVo {
    @ApiModelProperty(value = "code")
    private String code;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "数量")
    private Integer value;
    @ApiModelProperty(value = "设备总数")
    private Integer value1;
    @ApiModelProperty(value = "备件消耗数")
    private Integer value2;
}
