package com.aiurt.modules.sparepart.entity.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/22 19:06
 * @Version 1.0
 */
@Data
public class SparePartInQuery  {
    @ApiModelProperty("存放仓库")
    private String warehouseCode;
    @ApiModelProperty("备件类型")
    private String type;
    @ApiModelProperty("备件编号")
    private String materialCode;
    @ApiModelProperty("备件名称")
    private String materialName;
}
