package com.aiurt.boot.strategy.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author MrWei
 * @Date 2022/11/22 11:56
 **/
@Data
public class InspectionStyImportExcelDTO {
    /**
     * 年份
     */
    @Excel(name = "年份", width = 10, needMerge = true)
    @ApiModelProperty(value = "年份")
    private Integer year;
    /**
     * 策略名称
     */
    @Excel(name = "策略名称", width = 20, needMerge = true)
    @ApiModelProperty(value = "策略名称")
    private String name;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 25, needMerge = true)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**
     * 组织机构
     */
    @Excel(name = "组织机构", width = 25, needMerge = true)
    @ApiModelProperty(value = "组织机构")
    private String orgName;
    /**
     * 检修周期类型
     */
    @Excel(name = "检修周期类型", width = 15, needMerge = true)
    @ApiModelProperty(value = "检修周期类型")
    private String type;

    /**周期策略*/
    @Excel(name = "周期策略", width = 15)
    @ApiModelProperty(value = "周期策略")
    private java.lang.Integer tactics;
    /**
     * 是否需要审核：0否 1是
     */
    @Excel(name = "是否需要审核", width = 15, needMerge = true)
    @ApiModelProperty(value = "是否需要审核")
    private String isConfirm;
    /**
     * 是否需要验收：0否 1是
     */
    @Excel(name = "是否需要验收", width = 15, needMerge = true)
    @ApiModelProperty(value = "是否需要验收")
    private String isReceipt;

    /**
     * 作业类型（A1不用计划令,A2,A3,B1,B2,B3）
     */
    @Excel(name = "作业类型", width = 15, needMerge = true)
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private String workType;
    /**
     * 是否委外，0否1是
     */
    @Excel(name = "是否委外", width = 15, needMerge = true)
    @ApiModelProperty(value = "是否委外，0否1是")
    private String isOutsource;

    /**
     * 检修策略错误原因
     */
    @ApiModelProperty(value = "检修策略错误原因")
    @TableField(exist = false)
    private String InspectionStyErrorReason;

    @ExcelCollection(name = "检修标准")
    @ApiModelProperty(value = "检修标准")
    List<InspectionImportExcelDTO> inspectionExcelDTOList = new ArrayList<>();
}
