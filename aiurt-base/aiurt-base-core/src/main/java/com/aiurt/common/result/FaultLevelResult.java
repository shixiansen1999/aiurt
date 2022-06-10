package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author WangHongTao
 * @Date 2021/11/20
 */
@Data
public class FaultLevelResult {

    @ApiModelProperty(value = "故障发生时间")
    private Date createTime;

    @ApiModelProperty(value = "时长")
    private Long duration;

    @ApiModelProperty(value = "故障编号")
    private String faultCode;

    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;
}
