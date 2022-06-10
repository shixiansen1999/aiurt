package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/12
 */
@Data
public class FaultNumResult {

    /**故障数量*/
    @ApiModelProperty(value = "故障数量")
    private Integer faultNum;

    /**挂起数量*/
    @ApiModelProperty(value = "挂起数量")
    private Integer hangNum;

    /**自检数量*/
    @ApiModelProperty(value = "自检数量")
    private Integer checkNum;

    /**报修数量*/
    @ApiModelProperty(value = "报修数量")
    private Integer repairNum;

}
