package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.aiurt.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author km
 * @Date 2021/9/22 19:06
 * @Version 1.0
 */
@Data
public class SparePartInQuery extends PageVO {
    @ApiModelProperty("班组id")
    private String orgId;

    @ApiModelProperty("所属系统")
    private String systemCode;

    @ApiModelProperty("备件类型")
    private String materialType;

    @ApiModelProperty("备件编号")
    private String materialCode;

    @ApiModelProperty("备件名称")
    private String materialName;

    @ApiModelProperty("ids")
    List<Integer> selections;
}
