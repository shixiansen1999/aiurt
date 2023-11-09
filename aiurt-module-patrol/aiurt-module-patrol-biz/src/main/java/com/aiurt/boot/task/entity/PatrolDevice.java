package com.aiurt.boot.task.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 巡视任务设备关联表
 * @author sbx
 * @since 2023/10/18
 */
@Data
@TableName("patrol_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_device对象", description="patrol_device")
public class PatrolDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**巡视任务表id，关联patrol_task的id*/
    @ApiModelProperty(value = "巡视任务表id，关联patrol_task的id")
    private String taskId;
    /**巡检任务标准关联表ID，关联patrol_task_standard的id*/
    @ApiModelProperty(value = "巡检任务标准关联表ID，关联patrol_task_standard的id")
    private String taskStandardId;
    /**设备code*/
    @ApiModelProperty(value = "设备code")
    private String deviceCode;
}
