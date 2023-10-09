package com.aiurt.modules.sparepart.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2023/3/14
 * @desc
 */
@Data
public class WareHouseDTO {
    private String name;
    private Boolean isMyself;
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
}
