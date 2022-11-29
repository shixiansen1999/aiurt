package com.aiurt.boot.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/29
 * @desc
 */
@Data
@TableName("patrol_task_device_read_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_task_device_read_record对象", description="patrol_task_device_read_record")
public class PatrolTaskDeviceReadRecord {
    /**工单阅读登记表Id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "工单阅读登记表Id")
    private java.lang.String id;
    /**工单id*/
    @Excel(name = "工单id", width = 15)
    @ApiModelProperty(value = "工单id")
    private java.lang.String taskDeviceId;
    /**任务id*/
    @Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String taskId;
    /**专业code*/
    @Excel(name = "专业code", width = 15)
    @ApiModelProperty(value = "专业code")
    private java.lang.String majorCode;
    /**子系统code*/
    @Excel(name = "子系统code", width = 15)
    @ApiModelProperty(value = "子系统code")
    private java.lang.String subsystemCode;
    /**用户id*/
    @Excel(name = "用户id", width = 15)
    @ApiModelProperty(value = "用户id")
    private java.lang.String userId;
}
