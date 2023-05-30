package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class RecPersonDTO implements Serializable {

    private static final long serialVersionUID = 342894888212440771L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty
    private String userName;

    private String realName;

    private String key;

    private String value;

    private String label;
}
