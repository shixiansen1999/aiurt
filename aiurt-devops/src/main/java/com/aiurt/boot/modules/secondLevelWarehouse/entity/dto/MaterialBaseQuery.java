package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/28 18:31
 */
@Data
public class MaterialBaseQuery extends PageVO {
    @ApiModelProperty(value = "物资编号")
    private String code;

    @ApiModelProperty(value = "物资名称")
    private String name;

    @ApiModelProperty("存放仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;
}
