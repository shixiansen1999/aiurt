package com.aiurt.modules.fault.dto;

import com.aiurt.modules.basic.entity.DictEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author LKJ
 */
@Data
@ApiModel("综合大屏-年度故障维修情况")
public class FaultDataStatisticsDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @ApiModelProperty(value = "主键")
    private String id;

    /**线路编码*/
    @ApiModelProperty("线路编码")
    private String lineCode;

    @ApiModelProperty("线路名称")
    private String lineName;

    @ApiModelProperty("月份")
    private String month;

    @ApiModelProperty("第一天")
    private String firstDay;

    @ApiModelProperty("最后一天")
    private String lastDay;

    /**报修方式*/
    @ApiModelProperty("报修方式")
    private String faultModeCode;

    /**状态*/
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    private String subSystemCode;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统名称")
    private String subSystemName;

    @ApiModelProperty("故障数量")
    private Integer faultSum;


    @ApiModelProperty("报修故障数量百分比")
    private BigDecimal repairFaultNum;

    @ApiModelProperty("自检故障数量百分比")
    private BigDecimal selfCheckFaultNum;

    @ApiModelProperty("已完成故障数量百分比")
    private BigDecimal completedFaultNum;

    @ApiModelProperty("未完成故障数量百分比")
    private BigDecimal undoneFaultNum;

    @ApiModelProperty("报修故障数量")
    private BigDecimal repairFaults;

    @ApiModelProperty("自检故障数量")
    private BigDecimal selfCheckFaults;

    @ApiModelProperty("已完成故障数量")
    private BigDecimal completedFaults;

    @ApiModelProperty("未完成故障数量")
    private BigDecimal undoneFaults;

    @ApiModelProperty("类型:1：本周，2：上周，3：本月， 4：上月")
    private Integer boardTimeType;

    private List<String> majorCodes;

}
