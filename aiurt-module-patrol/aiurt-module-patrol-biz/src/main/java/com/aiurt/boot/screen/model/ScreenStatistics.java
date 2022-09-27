package com.aiurt.boot.screen.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
/**
 * @author JB
 * @Description: 大屏巡视模块-巡视数据统计对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ScreenStatistics", description = "大屏巡视模块-巡视数据统计对象")
public class ScreenStatistics {
    /**
     * 本周计划数
     */
    @ApiModelProperty(value = "本周计划数")
    private java.lang.Long planNum;
    /**
     * 本周完成数
     */
    @ApiModelProperty(value = "本周完成数")
    private java.lang.Long finishNum;
    /**
     * 本周漏检数
     */
    @ApiModelProperty(value = "本周漏检数")
    private java.lang.Long omitNum;
    /**
     * 巡视异常数
     */
    @ApiModelProperty(value = "巡视异常数")
    private java.lang.Long abnormalNum;
    /**
     * 今日巡视数
     */
    @ApiModelProperty(value = "今日巡视数")
    private java.lang.Long todayNum;
    /**
     * 今日巡视完成数
     */
    @ApiModelProperty(value = "今日巡视完成数")
    private java.lang.Long todayFinishNum;
}
