package com.aiurt.modules.robot.dto;

import com.aiurt.modules.robot.entity.TaskPathInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/279:08
 */
@Data
public class TaskPathInfoDTO extends TaskPathInfo {
    @ApiModelProperty("机器人名称")
    private String robotName;
    @ApiModelProperty(value = "机器人id")
    private java.lang.String robotId;
}
