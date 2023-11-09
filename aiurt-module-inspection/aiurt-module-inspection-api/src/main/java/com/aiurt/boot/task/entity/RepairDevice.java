package com.aiurt.boot.task.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 检修任务设备对象
 * @author sbx
 * @since 2023/10/18
 */
@Data
@TableName("repair_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_device对象", description="repair_device")
public class RepairDevice extends BaseEntity {
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
}
