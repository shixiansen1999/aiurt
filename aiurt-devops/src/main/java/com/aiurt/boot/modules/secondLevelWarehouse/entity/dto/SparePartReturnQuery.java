package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/22 11:20
 * @Version 1.0
 */
@Data
public class SparePartReturnQuery {
    @ApiModelProperty("所在班组")
    private String orgId;

    @ApiModelProperty("备件编号")
    private String materialCode;

    @ApiModelProperty("备件类型")
    private Integer type;

    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("ids")
    List<Integer> selections;

}
