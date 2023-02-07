package com.aiurt.boot.materials.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zwl
 */
@Data
@ApiModel("")
public class EmergencyMaterialsInvoicesReqDTO implements Serializable {

    private static final long serialVersionUID = 2199168375892692805L;

    @ApiModelProperty(value = "应急物资巡检单Id", required = true)
    private String  invoicesId;

    private Integer pageNo;

    private Integer pageSize;
}
