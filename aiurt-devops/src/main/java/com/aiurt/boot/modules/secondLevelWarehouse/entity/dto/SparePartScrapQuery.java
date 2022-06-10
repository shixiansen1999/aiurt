package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/23 16:40
 * @Version 1.0
 */
@Data
public class SparePartScrapQuery {
    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("备件类型")
    private Integer type;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty(value = "线路编号")
    private  String  lineCode;

    @ApiModelProperty("站点编号")
    private String stationCode;

    @ApiModelProperty("ids")
    List<Integer> selections;
}
