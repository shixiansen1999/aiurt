package com.aiurt.modules.sparepart.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/23 16:40
 * @Version 1.0
 */
@Data
public class SparePartScrapQuery  {
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备件类型")
    private Integer type;

    @ApiModelProperty("备件名称")
    private String materialName;
}
