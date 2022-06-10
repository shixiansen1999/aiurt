package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author WangHongTao
 * @Date 2021/11/20
 */
@Data
public class FaultPersonResult {

    @ApiModelProperty(value = "故障登记时间")
    private Date startDate;

    @ApiModelProperty(value = "维修完成时间")
    private Date endDate;

    @ApiModelProperty(value = "时长")
    private Long duration;


}
