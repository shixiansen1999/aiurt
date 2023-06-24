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
    private List<SparePartStockDTO> deviceChangeList;



    @ApiModelProperty(value = "易耗品")
    private List<SparePartStockDTO> consumableList;

    @ApiModelProperty(value = "是否合并 true 异常， false 正常")
    private Boolean isException;

    @ApiModelProperty(value = "故障原因id")
    private List<String> faultCauseSolutionId;
}
