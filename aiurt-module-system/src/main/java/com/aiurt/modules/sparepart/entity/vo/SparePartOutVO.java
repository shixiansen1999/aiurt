package com.aiurt.modules.sparepart.entity.vo;


import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/23 9:49
 * @Version 1.0
 */
@Data
public class SparePartOutVO extends SparePartOutOrder {
    @ApiModelProperty(value = "备件名称")
    private String materialName;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "备件类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产商")
    private String manufacturer;

}
