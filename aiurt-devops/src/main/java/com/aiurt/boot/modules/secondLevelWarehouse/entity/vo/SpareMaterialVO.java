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
}
