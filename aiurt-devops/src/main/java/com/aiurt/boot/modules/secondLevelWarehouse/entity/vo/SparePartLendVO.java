package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartLend;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/22 13:37
 * @Version 1.0
 */
@Data
public class SparePartLendVO  extends SparePartLend {
    @ApiModelProperty(value = "备件名称")
    private String materialName;

    @ApiModelProperty(value = "备件类型（1：非生产类型 2：生产类型）")
    private Integer materialType;

    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产商")
    private String manufacturer;


}
