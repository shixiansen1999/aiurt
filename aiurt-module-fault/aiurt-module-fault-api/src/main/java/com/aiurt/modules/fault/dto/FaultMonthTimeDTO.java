package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-15 14:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultMonthTimeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "月份总时长")
    private String  monthTime;

    @ApiModelProperty(value = "月份")
    private String month;

    @ApiModelProperty(value = "月份子系统时长")
    private List<FaultSystemTimeDTO> sysTimeList;



}
