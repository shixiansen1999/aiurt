package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/17 19:14
 * @Version 1.0
 */
@Data
public class SpareApplyMaterialDTO extends SparePartApplyMaterial {
    /**物资名称*/
    @ApiModelProperty(value = "物资名称")
    private String name;
    /**物资类型*/
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;
    /**规格类型*/
    @ApiModelProperty(value = "规格&型号")
    private String specifications;
    /**原产地*/
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产商*/
    @ApiModelProperty(value = "生产商")
    private String manufacturer;
    /**品牌*/
    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty("所在仓库编号")
    private String outWarehouseCode;

    @ApiModelProperty("所在仓库")
    private String outWarehouseName;

    @ApiModelProperty("现有库存")
    private Integer materialNum;

    @ApiModelProperty("所属部门")
    private String depart;

    @ApiModelProperty("所属系统")
    private String system;

    @ApiModelProperty("单位")
    private String unit;
}
