package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/22 16:44
 */
@Data
@ApiModel(value = "RepairPoolListVO", description = "检修计划池vo")
public class RepairPoolListVO {
    @ApiModelProperty(value = "检修ID")
    private Long id;
    @ApiModelProperty(value = "检修类型")
    private Integer type;
    @ApiModelProperty(value = "检修内容")
    private String repairPoolContent;
}
