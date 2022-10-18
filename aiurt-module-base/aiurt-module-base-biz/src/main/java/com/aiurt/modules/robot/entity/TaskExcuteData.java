package com.aiurt.modules.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: task_excute_data
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("task_excute_data")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "task_excute_data对象", description = "task_excute_data")
public class TaskExcuteData implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * task_id
     */
    @Excel(name = "task_id", width = 15)
    @ApiModelProperty(value = "task_id")
    private String taskId;
    /**
     * 任务名称
     */
    @Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
    /**
     * 任务类型
     */
    @Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型")
    private String taskType;
    /**
     * 任务状态
     */
    @Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态")
    private Integer taskStatus;
    /**
     * 机器人id
     */
    @Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private String robotId;
    /**
     * 当前巡检点Id
     */
    @Excel(name = "当前巡检点Id", width = 15)
    @ApiModelProperty(value = "当前巡检点Id")
    private String patrolDeviceId;
    /**
     * 当前巡检点名称
     */
    @Excel(name = "当前巡检点名称", width = 15)
    @ApiModelProperty(value = "当前巡检点名称")
    private String patrolDeviceName;
    /**
     * 异常数量
     */
    @Excel(name = "异常数量", width = 15)
    @ApiModelProperty(value = "异常数量")
    private Integer errorDeviceSize;
    /**
     * 已完成数量
     */
    @Excel(name = "已完成数量", width = 15)
    @ApiModelProperty(value = "已完成数量")
    private Integer finishDeviceSize;
    /**
     * 完成进度
     */
    @Excel(name = "完成进度", width = 15)
    @ApiModelProperty(value = "完成进度")
    private Integer taskFinishPercentage;
    /**
     * 点位总数
     */
    @Excel(name = "点位总数", width = 15)
    @ApiModelProperty(value = "点位总数")
    private Integer totalDeviceSize;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateBy;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
