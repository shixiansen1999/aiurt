package com.aiurt.modules.sparepart.entity.vo;

import com.aiurt.modules.sparepart.entity.SparePartScrap;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/23 16:33
 * @Version 1.0
 */
@Data
public class SparePartScrapVO extends SparePartScrap {
    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("物料类型")
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
}
