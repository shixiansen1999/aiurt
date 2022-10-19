package com.aiurt.modules.subsystem.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.math.BigDecimal;
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
    @ApiModelProperty(value = "子系统名称")
    @Excel(name = "子系统名称", width = 15)
    private String systemName;
    private String systemCode;
    private String deviceTypeCode;
    @ApiModelProperty(value = "设备类型名称")
    @Excel(name = "设备类型名称", width = 15)
    private String deviceTypeName;
    @ApiModelProperty(value = "故障总数")
    @Excel(name = "故障总数", width = 15)
    private Integer failureNum;
    @ApiModelProperty(value = "故障重大数")
    @Excel(name = "故障重大数", width = 15)
    private Integer seriousFaultNum;
    @ApiModelProperty(value = "故障一般数")
    @Excel(name = "故障一般数", width = 15)
    private Integer commonFaultNum;
    @ApiModelProperty(value = "故障时长")
    @Excel(name = "故障时长", width = 15)
    private BigDecimal failureDuration;
    Long num;
    String name;
    String code;
 //   @ExcelCollection(name = "设备类型", orderNum = "4")
    List<SubsystemFaultDTO> deviceTypeList;
}
