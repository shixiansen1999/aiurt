package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author km
 * @Date 2021/9/23 20:54
 * @Version 1.0
 */
@Data
@ExcelTarget("MaterialBaseInput")
public class MaterialBaseInput implements Serializable {

    @Excel(name="序号",width = 15)
    private String serialNumber;

    /**所属系统*/
    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    @NotNull
    private String system;

    /**大类编号*/
    @ApiModelProperty(value = "大类编号")
    private String typeCode;

    /**小类编号*/
    @ApiModelProperty(value = "小类编号")
    private String smallTypeCode;

    /**大类编号*/
    @Excel(name = "物资大类", width = 15)
    @ApiModelProperty(value = "大类编号")
    private String bigTypeName;

    /**小类编号*/
    @Excel(name = "物资小类", width = 15)
    @ApiModelProperty(value = "小类编号")
    private String smallTypeName;

    @ApiModelProperty(value = "所属系统")
    private String systemCode;

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String code;

    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private String name;

    /**物资类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private String typeName;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    /**规格型号*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;
    /**原产地*/
    @Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产厂家*/
    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂商")
    private String manufacturer;

    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    @NotNull
    private String unit;

    /**单价*/
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价（元）")
    @NotNull
    private BigDecimal price;

    /**图片*/
    @Excel(name = "图片", width = 15)
    @ApiModelProperty("图片")
    private String pictureUrl;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
}
