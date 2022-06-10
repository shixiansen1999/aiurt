package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/22 17:57
 */
@Data
public class DeviceQueryVO {
    @ApiModelProperty(value = "设置id",required = true)
    @NotNull(message = "设备ID为空")
    private Long deviceId;
    @ApiModelProperty(value = "周数")
    private Integer weeks;
    @ApiModelProperty(value = "检修人")
    private String repairName;
    @NotNull(message = "页码为空")
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNo;
    @NotNull(message = "页数为空")
    @ApiModelProperty(value = "页数",required = true)
    private Integer pageSize;
}
