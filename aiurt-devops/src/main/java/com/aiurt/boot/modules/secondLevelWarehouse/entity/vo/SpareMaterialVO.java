package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/22 15:42
 * @Version 1.0
 */
@Data
public class SpareMaterialVO {
    @ApiModelProperty("物资编号")
    private String materialCode;

    @ApiModelProperty("物资名称")
    private String materialName;

    @ApiModelProperty("物资库存数量")
    private Integer num;

    @ApiModelProperty(value = "备件类型")
    private  Integer  type;

    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产商")
    private String manufacturer;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "所在仓库")
    private String warehouseName;

    @ApiModelProperty("线路")
    private String lineName;

    @ApiModelProperty("站点")
    private String stationName;
}
