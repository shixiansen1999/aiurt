package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/18 14:01
 * @Version 1.0
 */
@Data
public class StockInOrderLevel2VO extends StockInOrderLevel2 {
    @ApiModelProperty("入库仓库名称")
    private String warehouseName;
    @ApiModelProperty("操作人名称")
    private String operatorName;
}
