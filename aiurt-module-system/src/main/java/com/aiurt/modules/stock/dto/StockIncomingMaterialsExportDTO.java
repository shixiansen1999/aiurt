package com.aiurt.modules.stock.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zwl
 * @version 1.0
 * @date 2022/1/17
 * @desc
 */
@Data
public class StockIncomingMaterialsExportDTO implements Serializable {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**专业编码*/
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
    @TableField(exist = false)
    private  String  majorCode;

    /**专业编码*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "专业名称")
    @Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
    @TableField(exist = false)
    private  String  majorName;

    /**子系统编号*/
    @ApiModelProperty(value = "子系统编号")
    @Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
    @TableField(exist = false)
    private  String  systemCode;

    /**子系统编号*/
    @Excel(name = "所属子系统", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
    @TableField(exist = false)
    private  String  systemName;

    /**分类编码层级*/
    @ApiModelProperty(value = "分类编码层级")
    @TableField(exist = false)
    @Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "base_type_code")
    private  String  baseTypeCode;

    /**分类编码层级*/
    @ApiModelProperty(value = "分类编码层级")
    @TableField(exist = false)
    private  String  baseTypeCodeCc;

    /**分类编码层级名称*/
    @Excel(name = "物资分类", width = 15)
    @ApiModelProperty(value = "分类编码层级名称")
    @TableField(exist = false)
    private  String  baseTypeCodeCcName;

    /**物资编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资编码")
    @Excel(name = "物资编码", width = 15)
    private String materialCode;


    /**物资名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资名称")
    @Excel(name = "物资名称", width = 15)
    private String materialName;

    /**类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "类型")
    @Dict(dicCode = "material_type")
    @TableField(exist = false)
    private  String  type;

    /**入库数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库数量")
    @Excel(name = "入库数量", width = 15)
    private Integer number;


    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = " 单位")
    @Dict(dicCode = "materian_unit")
    @TableField(exist = false)
    private  String  unit;
}
