package com.aiurt.boot.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author WangHongTao
 * @Date 2021/11/21
 */

@Data
public class FaultCodesResult implements Serializable {

    /**code*/
    @ApiModelProperty(value = "code")
    private String code;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

}
