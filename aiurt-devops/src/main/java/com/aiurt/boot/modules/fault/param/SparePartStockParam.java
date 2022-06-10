package com.aiurt.boot.modules.fault.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/9
 */
@Data
public class SparePartStockParam {

    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("位置")
    private String orgId;
}
