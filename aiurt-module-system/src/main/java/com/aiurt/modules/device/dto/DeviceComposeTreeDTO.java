package com.aiurt.modules.device.dto;

import com.aiurt.modules.base.BaseTreeDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author fgw
 */
@Data


public class DeviceComposeTreeDTO extends BaseTreeDTO {
    private static final long serialVersionUID = 2226381842334442517L;

    @ApiModelProperty(value = "子节点")
    private List<DeviceComposeTreeDTO> children;

}
