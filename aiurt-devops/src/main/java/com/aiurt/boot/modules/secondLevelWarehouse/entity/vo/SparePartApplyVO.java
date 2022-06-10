package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/24 16:47
 * @Version 1.0
 */
@Data
public class SparePartApplyVO extends SparePartApply {

    @ApiModelProperty("申领仓库名称")
    private String warehouseName;

    @ApiModelProperty("出库仓库名称")
    private String outWarehouseName;

    @ApiModelProperty("保管人")
    private String operatorName;

    @ApiModelProperty("所属部门")
    private String department;

    @ApiModelProperty("出库数量")
    private Integer num;

    @ApiModelProperty("申领数量")
    private Integer applyNum;

    @ApiModelProperty("物资类型")
    private Integer typeName;

}
