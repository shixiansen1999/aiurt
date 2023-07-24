package com.aiurt.modules.fault.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 大屏-地图-当前故障-现象下拉框的返回对象
 * 返回的是 未完成故障（挂起+维修中）的故障现象、故障发生时间、故障code
 * @author 华宜威
 * @date 2023-07-03 10:54:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaultUnfinishedDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**故障code*/
    @ApiModelProperty(value = "故障code")
    private String code;

    /**故障现象分类code*/
    @ApiModelProperty(value = "故障现象分类code")
    private String faultPhenomenon;

    /**故障现象分类name*/
    @ApiModelProperty(value = "故障现象分类name")
    private String faultPhenomenonName;

    /**故障发生时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy年MM月dd日 HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间")
    private Date happenTime;
}
