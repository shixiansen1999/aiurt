package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/12
 */
@Data
public class TimeOutFaultNum {

    /**超时故障数量*/
    @ApiModelProperty(value = "超时故障数量")
    private Integer timeOutFaultNum;

    /**一级故障数量*/
    @ApiModelProperty(value = "一级故障数量")
    private Integer firstLevelFaultNum;

    /**二级故障数量*/
    @ApiModelProperty(value = "二级故障数量")
    private Integer secondLevelFaultNum;

    /**三级故障数量*/
    @ApiModelProperty(value = "三级故障数量")
    private Integer thirdLevelFaultNum;

    /**超时挂起故障数量*/
    @ApiModelProperty(value = "超时挂起故障数量")
    private Integer timeOutHangNum;
}
