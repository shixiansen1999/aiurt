package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.swsc.copsms.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author km
 * @Date 2021/9/22 11:20
 * @Version 1.0
 */
@Data
public class SparePartLendQuery extends PageVO {
    @ApiModelProperty("仓库编号")
    private String warehouseCode;

    @ApiModelProperty("备件类型")
    private Integer type;

    @ApiModelProperty("备件名称")
    private String materialName;

}
