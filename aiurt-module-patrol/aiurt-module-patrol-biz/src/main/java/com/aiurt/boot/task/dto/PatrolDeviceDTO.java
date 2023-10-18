package com.aiurt.boot.task.dto;

import com.aiurt.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolDeviceDTO extends BaseEntity {

    /**巡视任务表id，关联patrol_task的id*/
    @ApiModelProperty(value = "巡视任务表id，关联patrol_task的id")
    private String taskId;
    /**巡检任务标准关联表ID，关联patrol_task_standard的id*/
    @ApiModelProperty(value = "巡检任务标准关联表ID，关联patrol_task_standard的id")
    private String taskStandardId;
    /**设备code*/
    @ApiModelProperty(value = "设备code")
    private String deviceCode;
    @ApiModelProperty(value = "")
    private String deviceName;
}
