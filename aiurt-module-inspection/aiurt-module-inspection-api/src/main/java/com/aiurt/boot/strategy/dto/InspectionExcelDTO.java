package com.aiurt.boot.strategy.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.util.List;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 12:12
 **/
@Data
public class InspectionExcelDTO {
    @ApiModelProperty(value = "检修计划策略标准关联表Id")
    private java.lang.String Id;
    /**检修标准编码*/
    @Excel(name = "检修标准编码", width = 20,needMerge = true)
    @ApiModelProperty(value = "检修标准编码")
    private java.lang.String code;
    /**检修标准名称*/
    @Excel(name = "检修标准名称", width = 25,needMerge = true)
    @ApiModelProperty(value = "检修标准名称")
    private java.lang.String title;

    @ExcelCollection(name = "所选设备")
    @ApiModelProperty(value = "所选设备")
    private List<DeviceExcelDTO> deviceExcelDTOS;
}
