package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.modules.material.entity.MaterialBaseType;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * @Author zwl
 * @Date 2022/10/17
 * @Version 1.0
 */
@Data
public class SparePartStatistics {

    /**主键id*/
    @ApiModelProperty(value = "主键id")
    @TableField(exist = false)
    private  String  id;


    /**子系统编码*/
    @ApiModelProperty(value = "子系统编码")
    @TableField(exist = false)
    private String systemCode;

    /**物资分类编码*/
    @ApiModelProperty(value = "物资分类编码")
    @TableField(exist = false)
    private  String  baseTypeCode;


    /**子系统名称*/
    @Excel(name = "子系统名称", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private String systemName;


    /**分类名称*/
    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    @TableField(exist = false)
    private  String  baseTypeName;

    /**子系统名称*/
    @Excel(name = "二级库数量", width = 15)
    @ApiModelProperty(value = "二级库数量")
    @TableField(exist = false)
    private Long twoCount;

    /**子系统名称*/
    @Excel(name = "三级库数量", width = 15)
    @ApiModelProperty(value = "三级库数量")
    @TableField(exist = false)
    private Long threeCount;

    /**上两年度总消耗量*/
    @Excel(name = "上两年度总消耗量", width = 15)
    @ApiModelProperty(value = "上两年度总消耗量")
    @TableField(exist = false)
    private Long twoTotalConsumption;

    /**上两年度月均消耗量*/
    @Excel(name = "上两年度月均消耗量", width = 15)
    @ApiModelProperty(value = "上两年度月均消耗量")
    @TableField(exist = false)
    private String twoMonthConsumption;

    /**上年度总消耗量*/
    @Excel(name = "上年度总消耗量", width = 15)
    @ApiModelProperty(value = "上年度总消耗量")
    @TableField(exist = false)
    private Long lastYearConsumption;

    /**上年度月均消耗量*/
    @Excel(name = "上年度月均消耗量", width = 15)
    @ApiModelProperty(value = "上年度月均消耗量")
    @TableField(exist = false)
    private String lastYearMonthConsumption;

    /**本年度总消耗量*/
    @Excel(name = "本年度总消耗量", width = 15)
    @ApiModelProperty(value = "本年度总消耗量")
    @TableField(exist = false)
    private Long thisYearConsumption;

    /**本年度月均消耗量*/
    @Excel(name = "本年度月均消耗量", width = 15)
    @ApiModelProperty(value = "本年度月均消耗量")
    @TableField(exist = false)
    private String thisYearMonthConsumption;

    /**上个月的消耗量*/
    @Excel(name = "上个月的消耗量", width = 15)
    @ApiModelProperty(value = "上个月的消耗量")
    @TableField(exist = false)
    private Long lastMonthConsumption;

    /**本月的消耗量*/
    @Excel(name = "本月的消耗量", width = 15)
    @ApiModelProperty(value = "本月的消耗量")
    @TableField(exist = false)
    private Long thisMonthConsumption;

    /**本月的消耗量*/
    @ApiModelProperty(value = "导出参数")
    @TableField(exist = false)
    private String exportColumns;

    @ApiModelProperty("分页参数")
    private Integer pageNo;


    @ApiModelProperty("分页参数")
    private Integer pageSize;

    /**子系统人员*/
    @ApiModelProperty(value = "子系统下的物资分类")
    @TableField(exist = false)
    private List<MaterialBaseType> materialBaseTypeList;

}
