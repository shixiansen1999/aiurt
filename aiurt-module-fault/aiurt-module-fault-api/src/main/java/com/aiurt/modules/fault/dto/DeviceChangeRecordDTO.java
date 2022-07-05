package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceChangeRecordDTO implements Serializable {

    /**
     * 组件更换
     */
    @ApiModelProperty(value = "组件更换")
    private List<DeviceChangeDTO> deviceChangeList;



    @ApiModelProperty(value = "易耗品")
    private List<DeviceChangeDTO> consumableList;
}
