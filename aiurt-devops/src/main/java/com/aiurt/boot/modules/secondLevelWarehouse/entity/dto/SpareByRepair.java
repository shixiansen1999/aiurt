package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartWithWarehouseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *      填写维修记录时 原备件到报损模块，新备件进行仓库出库
 * @Author: km
 * DateTime: 2021/9/25 19:34
 */
@Data
public class SpareByRepair {

    @ApiModelProperty(value = "故障编号")
    private String faultCode;

    @ApiModelProperty("原备件列表")
    private List<SparePartWithWarehouseVO> oldMaterialList;

    @ApiModelProperty("新备件列表")
    private List<SparePartWithWarehouseVO> newMaterialList;
}
