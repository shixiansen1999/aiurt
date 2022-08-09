package com.aiurt.modules.flow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title: 转办任务DTO
 * @Description:
 * @date 2022/8/810:10
 */
@Data
public class TurnTaskDTO {
    @ApiModelProperty("任务id")
    private String taskId;
    @ApiModelProperty("被转办的人员username")
    private String username;

}
