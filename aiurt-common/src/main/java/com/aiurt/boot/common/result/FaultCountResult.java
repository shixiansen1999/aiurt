package com.aiurt.boot.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 */
@Data
public class FaultCountResult {


    @ApiModelProperty(value = "系统编号")
    private String systemCode;

    @Excel(name = "设备分类", width = 15)
    @ApiModelProperty(value = "系统")
    private String systemName;

    @Excel(name = "自检", width = 15)
    @ApiModelProperty(value = "自检")
    private Integer selfCheckNum;

    @Excel(name = "报修", width = 15)
    @ApiModelProperty(value = "报修")
    private Integer repairNum;

    @Excel(name = "合计", width = 15)
    @ApiModelProperty(value = "合计")
    private Integer sumNum;

    @ApiModelProperty(value = "上月同期数量")
    private Integer thanLastMonthNum;

    @Excel(name = "与上月对比（%）", width = 15)
    @ApiModelProperty(value = "与上月对比（%）")
    private String thanLastMonth;

    @ApiModelProperty(value = "去年同期数量")
    private Integer thanLastYearNum;

    @Excel(name = "与去年同期对比（%）", width = 15)
    @ApiModelProperty(value = "与去年同期对比（%）")
    private String thanLastYear;

    @Excel(name = "设备故障", width = 15)
    @ApiModelProperty(value = "设备故障")
    private Integer deviceFaultNum;

    @Excel(name = "线路故障", width = 15)
    @ApiModelProperty(value = "线路故障")
    private Integer lineFaultNum;

    @Excel(name = "电源故障", width = 15)
    @ApiModelProperty(value = "电源故障")
    private Integer powerSupplyFaultNum;

    @Excel(name = "外界妨害", width = 15)
    @ApiModelProperty(value = "外界妨害")
    private Integer externalNum;

    @Excel(name = "其他", width = 15)
    @ApiModelProperty(value = "其他")
    private Integer otherNum;

    @Excel(name = "各系统百分比", width = 15)
    @ApiModelProperty(value = "各系统百分比")
    private String systemPercentage;

}
