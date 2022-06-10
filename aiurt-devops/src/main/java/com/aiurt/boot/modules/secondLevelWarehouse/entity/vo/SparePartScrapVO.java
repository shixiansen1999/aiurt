package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartScrap;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author km
 * @Date 2021/9/23 16:33
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SparePartScrapVO extends SparePartScrap {
    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("物料类型")
    private Integer type;

    @ApiModelProperty("物料类型名称")
    private String typeName;

    @ApiModelProperty("线路名称")
    private String lineName;

    @ApiModelProperty("站点名称")
    private String stationName;
    /**规格类型*/
    @ApiModelProperty(value = "规格&型号")
    private String specifications;
    /**原产地*/
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产商*/
    @ApiModelProperty(value = "生产商")
    private String manufacturer;

    /**状态*/
    @ApiModelProperty(value = "状态")
    private String statusDesc;

}
