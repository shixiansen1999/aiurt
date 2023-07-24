package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-13 15:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultDataAnalysisCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 故障总数
     */
    @ApiModelProperty(value = "故障总数")
    private Integer sum;

    /**
     * 未修复故障数
     */
    @ApiModelProperty(value = "未修复故障数")
    private Integer unSolve;

    /**
     * 挂起故障数
     */
    @ApiModelProperty(value = "挂起故障数")
    private Integer hangUpNum;

    /**
     * 今日新增数量
     */
    @ApiModelProperty(value = "当日新增数")
    private Integer todayAdd;

    /**
     * 今日修复数量
     */
    @ApiModelProperty(value = "今日修复")
    private Integer todaySolve;

    /**
     * 本周新增数量
     */
    @ApiModelProperty(value = "本周新增数量")
    private Integer weekAdd;

    /**
     * 本周修复数量
     */
    @ApiModelProperty(value = "本周修复数量")
    private Integer weekSolve;

}
