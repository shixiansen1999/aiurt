package com.aiurt.boot.task.dto;

import com.aiurt.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author sbx
 * @since 2023/10/19
 */
@Data
public class RepairDeviceDTO extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**检修任务表id，关联repair_task的id*/
    @ApiModelProperty(value = "检修任务表id，关联repair_task的id")
    private String taskId;
    /**检修任务标准关联表ID，关联repair_task_standard_rel的id*/
    @ApiModelProperty(value = "检修任务标准关联表ID，关联repair_task_standard_rel的id")
    private String taskStandardId;
    /**设备code*/
    @ApiModelProperty(value = "设备code")
    private String deviceCode;
    /**设备名称*/
    @ApiModelProperty(value = "设备名称")
    private String deviceName;
}
