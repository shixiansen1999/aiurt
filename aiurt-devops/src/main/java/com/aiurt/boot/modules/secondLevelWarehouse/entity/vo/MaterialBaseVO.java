package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/28 18:34
 */
@Data
public class MaterialBaseVO extends MaterialBase {
    @ApiModelProperty("所在仓库")
    private String warehouseName;
}
