package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartInOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/22 19:24
 * @Version 1.0
 */
@Data
public class SparePartInVO extends SparePartInOrder {
    @ApiModelProperty(value = "所属系统")
    private String systemCode;

    @ApiModelProperty("物资名称")
    private String materialName;

    @ApiModelProperty("物资类型")
    private Integer type;

    @ApiModelProperty("物资类型名称")
    private String typeName;

    @ApiModelProperty("规格")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "存放仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "确认状态描述")
    private String confirmStatusDesc;

    @ApiModelProperty("保管人")
    private String keeperName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    private  java.util.Date  joinTime;


}
