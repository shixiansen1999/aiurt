package com.aiurt.modules.online.workflowapi.dto;

import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author:wgp
 * @create: 2023-09-05 10:01
 * @Description:
 */
@Data
public class ActCustomInterfaceDTO extends ActCustomInterface {
    @ApiModelProperty("模块名称")
    private String module_dictText;
}
