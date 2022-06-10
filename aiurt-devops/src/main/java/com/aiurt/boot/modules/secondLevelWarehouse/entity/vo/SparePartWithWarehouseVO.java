package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/25 20:01
 */
@Data
public class SparePartWithWarehouseVO {
    @ApiModelProperty("仓库编号")
    private String warehouseCode;

    @ApiModelProperty("物资编号")
    private String materialCode;

    @ApiModelProperty("物资数量")
    private Integer num;
}
