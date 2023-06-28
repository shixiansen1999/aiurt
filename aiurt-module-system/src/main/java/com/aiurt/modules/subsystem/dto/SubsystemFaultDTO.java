package com.aiurt.modules.subsystem.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.subsystem.dto
 * @className: SubsystemFaultDTO
 * @author: life-0
 * @date: 2022/10/17 11:13
 * @description: TODO
 * @version: 1.0
 */
@Data
public class SubsystemFaultDTO {
    String id;
    private String systemName;
    @ApiModelProperty(value = "子系统简称")
    @Excel(name = "专业子系统", width = 15)
    private String shortenedForm;
    private String systemCode;
    private List<String> systemCodes;
    private String deviceTypeCode;
 /*   @ApiModelProperty(value = "设备类型名称")
    @Excel(name = "设备类型名称", width = 15)
    private String deviceTypeName;*/
    @ApiModelProperty(value = "故障总数")
    @Excel(name = "故障总数", width = 15)
    private Integer failureNum;
    @ApiModelProperty(value = "解决数")
    private Integer solutionsNum;
    @ApiModelProperty(value = "故障重大数")
    @Excel(name = "故障重大数", width = 15)
    private Integer seriousFaultNum;
    @ApiModelProperty(value = "故障一般数")
    @Excel(name = "故障一般数", width = 15)
    private Integer commonFaultNum;
    @ApiModelProperty(value = "故障时长")
    @Excel(name = "故障时长（秒）", width = 15)
    private Integer failureDuration;
    /**故障维修时长*/
    @Excel(name = "故障维修时长（秒）", width = 15)
    @ApiModelProperty(value = "故障维修时长")
    private Integer repairDuration;

    /**维修响应时长*/
    @ApiModelProperty(value = "维修响应时长")
    private Integer responseDuration;
    @ApiModelProperty(value = "关闭率")
    @Excel(name = "关闭率（%）", width = 15)
    private Integer solutionsRate;
    @ApiModelProperty(value = "可靠度")
    @Excel(name = "可靠度（%）", width = 15)
    private String  reliability;

    @ApiModelProperty("平均响应时间")
    @Excel(name = "平均故障响应时间（秒）", width = 15)
    private Integer averageTime;
    @ApiModelProperty("平均维修时间")
    @Excel(name = "平均故障修复时间（秒）", width = 15)
    private Integer averageFaultTime;

    Integer num;
    String name;
    String code;

    @ApiModelProperty(value = "故障率")
    private String  failureRate;
    @ApiModelProperty(value = "日期")
    private String yearMonth;
    /**
     * 设备类型
     */
    List<SubsystemFaultDTO> deviceTypeList;
}
