package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @Author km
 * @Date 2021/9/16 17:55
 * @Version 1.0
 */
@Data
public class StockInDetailVO{

    @ApiModelProperty(value = "主键id")
    private  Long  id;

    @ApiModelProperty(value = "所属部门")
    private String department;

    @ApiModelProperty(value = "所属系统")
    private String system;

    @ApiModelProperty(value = "物资编号")
    private String materialCode;

    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty("存放位置")
    private String warehouseName;

    @ApiModelProperty("单价")
    private Integer price;

    @ApiModelProperty("总价")
    private Integer totalPrice;

    @ApiModelProperty("数量")
    private Integer num;

    @ApiModelProperty("备注")
    private String remarks;




}
