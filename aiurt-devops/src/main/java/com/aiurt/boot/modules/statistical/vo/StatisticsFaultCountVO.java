package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author renanfeng
 * @version 1.0
 * @date 2022/01/25 13:30
 */
@Data
public class StatisticsFaultCountVO {
    @ApiModelProperty(value = "故障总数")
    private int faultTotal;
    @ApiModelProperty(value = "未修复故障")
    private int unCompleteCount;
    @ApiModelProperty(value = "本周增加")
    private int faultWeekCount;
    @ApiModelProperty(value = "本周修复")
    private int faultWeekCompleteCount;
    @ApiModelProperty(value = "维修列表")
    private List<FaultSystemVO> statisticsFaultSystemVOList;
}
