package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.modules.sparepart.entity.SparePartStock;
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

    @ApiModelProperty("规格")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产商")
    private String manufacturer;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty("仓库名称")
    private String warehouseName;


}
