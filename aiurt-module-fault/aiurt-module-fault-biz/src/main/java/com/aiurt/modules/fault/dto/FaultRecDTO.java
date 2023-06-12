package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 历史维修任务DTO
 *
 * @author wgp
 */
@Data
public class FaultRecDTO {
    @ApiModelProperty("历史任务描述：格式：设备类型/故障原因")
    private String faultRec;
    @ApiModelProperty("是否染色，true代表需要染色，false代表不用染色")
    private Boolean isColored;
}
