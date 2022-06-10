package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author km
 * @Date 2021/9/18 16:25
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDetailVO extends StockLevel2CheckDetail {
    @ApiModelProperty("仓库名称")
    private String warehouseName;
    @ApiModelProperty("物资名称")
    private String materialName;
    @ApiModelProperty("物资类型")
    private Integer type;
    @ApiModelProperty("规格")
    private String specifications;
    @ApiModelProperty("账面价值")
    private BigDecimal bookPrice;
    @ApiModelProperty("物资单价")
    private BigDecimal price;
    @ApiModelProperty("账面数量")
    private Integer bookNum;
}
