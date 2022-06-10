package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartOutOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/23 9:49
 * @Version 1.0
 */
@Data
public class SparePartOutVO extends SparePartOutOrder {
    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "所在仓库")
    private String warehouseName;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @ApiModelProperty(value = "物资类型名称（1：非生产类型 2：生产类型）")
    private String typeName;

    @ApiModelProperty(value = "规格型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "品牌")
    private  String  brand;

    @ApiModelProperty(value = "所属系统")
    private String system;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "单价")
    private  Double  price;

}
