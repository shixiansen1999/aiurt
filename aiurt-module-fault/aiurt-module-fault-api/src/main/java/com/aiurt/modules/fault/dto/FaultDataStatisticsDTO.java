package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author LKJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultDataStatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @ApiModelProperty(value = "主键")
    private String id;

    /**线路编码*/
    @ApiModelProperty("线路编码")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;

    @ApiModelProperty("月份")
    private String month;

    @ApiModelProperty("月份第一天")
    private String firstDay;

    @ApiModelProperty("月份最后一天")
    private String lastDay;


    /**报修方式*/
    @ApiModelProperty("报修方式")
    private String faultModeCode;

    /**状态*/
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty("故障数量")
    private Integer faultSum;

    @ApiModelProperty("报修故障数量")
    private Integer repairFaultNum;

    @ApiModelProperty("自检故障数量")
    private Integer selfCheckFaultNum;

    @ApiModelProperty("已完成故障数量")
    private Integer completedFaultNum;

    @ApiModelProperty("未完成故障数量")
    private Integer undoneFaultNum;

}
