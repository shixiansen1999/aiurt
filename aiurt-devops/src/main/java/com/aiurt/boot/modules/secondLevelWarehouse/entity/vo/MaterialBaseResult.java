package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author WangHongTao
 * @Date 2021/12/29
 */
@Data
public class MaterialBaseResult {

    /**所属系统*/
    @ApiModelProperty(value = "所属系统")
    private String systemName;

    /**物资大类*/
    @ApiModelProperty(value = "物资大类")
    private String bigTypeName;

    /**物资小类*/
    @ApiModelProperty(value = "物资小类")
    private String smallTypeName;

    /**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private String code;

    /**物资名称*/
    @ApiModelProperty(value = "物资名称")
    private String name;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    /**物资类型*/
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private String typeName;

    /**规格型号*/
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    /**生产厂家*/
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    /**单位*/
    @ApiModelProperty(value = "单位")
    private String unit;

    /**单价*/
    @ApiModelProperty(value = "单价（元）")
    private BigDecimal price;

    /**
     * 单价
     */
    @ApiModelProperty(value = "总价（元）")
    private BigDecimal total;

    /**图片*/
    @ApiModelProperty("图片")
    private String pictureUrl;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remark;

}
