package com.aiurt.boot.standard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.standard.dto
 * @className: InspectionStandardDto
 * @author: life-0
 * @date: 2022/6/22 14:43
 * @description: TODO
 * @version: 1.0
 */
@Data
public class InspectionStandardDto {
    /**专业code*/
    @Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称")
    private java.lang.String professionName;
    /**适用系统code*/
    @Excel(name = "适用系统code", width = 15)
    @ApiModelProperty(value = "适用系统code")
    private java.lang.String subsystemCode;
    @Excel(name = "适用系统名称", width = 15)
    @ApiModelProperty(value = "适用系统名称")
    private java.lang.String subsystemName;
    @Excel(name = "设备类型code", width = 15)
    @ApiModelProperty(value = "设备类型code")
    private java.lang.String deviceTypeCode;
    @Excel(name = "设备类型名称", width = 15)
    @ApiModelProperty(value = "设备类型名称")
    private java.lang.String deviceTypeName;

}
