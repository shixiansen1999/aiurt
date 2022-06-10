package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author km
 * @Date 2021/9/17 19:44
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StockDTO {
    @ApiModelProperty("出库单的id")
    private Long id;

    @ApiModelProperty("数量")
    private Integer num;
}
