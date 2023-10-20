package com.aiurt.boot.task.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 检修结果异常设备对象
 * @author sbx
 * @since 2023/10/18
 */
@Data
@TableName("repair_abnormal_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_abnormal_device对象", description="repair_abnormal_device")
public class RepairAbnormalDevice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**检修单巡检结果表id，关联repair_task_result的id*/
    @ApiModelProperty(value = "检修单巡检结果表id，关联repair_task_result的id")
    private String resultId;
    /**异常设备code*/
    @ApiModelProperty(value = "异常设备code")
    private String deviceCode;
}
