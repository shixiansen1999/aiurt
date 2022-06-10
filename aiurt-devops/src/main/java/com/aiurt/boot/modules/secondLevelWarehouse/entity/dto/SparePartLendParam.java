package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author WangHongTao
 * @Date 2021/11/17
 */
@Data
public class SparePartLendParam {

    @ApiModelProperty("所在班组")
    private String orgId;

    @ApiModelProperty("物资类型")
    private Integer type;

    @ApiModelProperty("线路")
    private String lineCode;

    @ApiModelProperty("站点")
    private String stationCode;

    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("ids")
    List<Integer> selections;
}
