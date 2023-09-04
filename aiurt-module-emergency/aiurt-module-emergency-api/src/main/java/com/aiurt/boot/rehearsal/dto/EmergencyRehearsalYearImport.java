package com.aiurt.boot.rehearsal.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class EmergencyRehearsalYearImport {

    /**计划名称*/
    @Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
    private java.lang.String name;
    /**所属年份格式：yyyy*/
    @Excel(name = "所属年份", width = 15)
    @ApiModelProperty(value = "所属年份格式：yyyy")
    private java.lang.String year;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    private String mistake;
    /**
     * 月演练计划列表
     */
    @ExcelCollection(name = "演练计划")
    @ApiModelProperty(value = "月演练计划列表")
    private List<EmergencyRehearsalMonthImport> monthList;
}
