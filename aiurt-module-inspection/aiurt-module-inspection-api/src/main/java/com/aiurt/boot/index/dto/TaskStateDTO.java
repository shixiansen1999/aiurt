package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 任务状态DTO
 * @date 2022/9/814:32
 */
@Data
public class TaskStateDTO {
    @ApiModelProperty("站点编码")
    private String stationCode;
    @ApiModelProperty("任务状态字符串")
    private String statusStr;
}
