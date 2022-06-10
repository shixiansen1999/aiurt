package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/16 14:23
 * @Version 1.0
 */
@Data
public class MaterialVO {
    @ApiModelProperty("物资编号")
    private String materialCode;

    @ApiModelProperty("物资数量")
    private Integer materialNum;
}
