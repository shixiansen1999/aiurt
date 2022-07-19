package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 下拉列表
 * @date 2022/6/2318:50
 */
@Data
public class StandardNewDTO {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("检修标准")
    private String id;
}
