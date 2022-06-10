package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStock;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/17 17:12
 * @Version 1.0
 */
@Data
public class SparePartStockDTO extends SparePartStock {

    @ApiModelProperty("物资名称")
    private String materialName;

    @ApiModelProperty("物资类型")
    private Integer materialType;

    @ApiModelProperty("物资类型名称")
    private String materialTypeString;

    @ApiModelProperty("规格型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty("存放位置")
    private String warehouseName;

    @ApiModelProperty("价格")
    private Integer price;

    @ApiModelProperty(value = "所属部门")
    private String department;

    @ApiModelProperty(value = "所属系统")
    private String systemCode;


    @ApiModelProperty(value = "物资大类")
    private String bigTypeName;

    @ApiModelProperty(value = "物资小类")
    private String smallTypeName;

    @ApiModelProperty(value = "总价")
    private String total;
}
