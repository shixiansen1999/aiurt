package com.aiurt.boot.task.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 巡视任务标准关联的设备类型表
 * @author sbx
 * @since 2023/11/6
 */
@Data
@TableName("patrol_device_type")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_device_type对象", description="patrol_device_type")
public class PatrolDeviceType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**巡视任务id*/
    @ApiModelProperty(value = "巡视任务id")
    private String taskId;
    /**巡视任务标准关联表id*/
    @ApiModelProperty(value = "巡视任务标准关联表id")
    private String taskStandardId;
    /**设备类型code*/
    @ApiModelProperty(value = "设备类型code")
    private String deviceTypeCode;
}
