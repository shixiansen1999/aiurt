package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/18 18:51
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDetailEditDTO {
    @ApiModelProperty("盘点详情行id")
    private Long id;

    @ApiModelProperty(value = "实盘数量")
    private  Integer  actualNum;

    @ApiModelProperty(value = "盘盈数量")
    private  Integer  profitNum;

    @ApiModelProperty(value = "盘亏数量")
    private  Integer  lossNum;

    @ApiModelProperty(value = "备注")
    private  String  note;
}
