package com.aiurt.modules.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAssemblyDTO implements Serializable {

    private static final long serialVersionUID = 342894888212440771L;

    @ApiModelProperty(value = "组件")
    private String code;

    @ApiModelProperty(value = "组件名称")
    private String materialName;


    private String key;

    private String value;

    private String label;

    private String title;
}
