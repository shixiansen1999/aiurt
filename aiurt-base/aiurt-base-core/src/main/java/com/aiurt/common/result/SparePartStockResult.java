package com.aiurt.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/9
 */
@Data
public class SparePartStockResult {

    @ApiModelProperty("备件编号")
    private String materialCode;

    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("位置")
    private String location;

    @ApiModelProperty("数量")
    private String num;
}
