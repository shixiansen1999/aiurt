package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/18 16:19
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDetailDTO {
    @ApiModelProperty(value = "盘点任务单号")
    private String stockCheckCode;
    @ApiModelProperty("物资编码")
    private String materialCode;
    @ApiModelProperty("物资名称")
    private String materialName;
}
