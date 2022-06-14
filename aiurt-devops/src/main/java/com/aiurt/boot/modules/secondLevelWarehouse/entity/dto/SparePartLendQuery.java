package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/22 11:20
 * @Version 1.0
 */
@Data
public class SparePartLendQuery extends PageVO {

    @ApiModelProperty("所在班组1")
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
