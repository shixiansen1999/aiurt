package com.aiurt.modules.robot.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: task_excute_data
 * @Author: aiurt
 * @Date:   2022-09-28
 * @Version: V1.0
 */
@Data
@TableName("task_excute_data")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="task_excute_data对象", description="task_excute_data")
public class TaskExcuteData implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**task_id*/
	@Excel(name = "task_id", width = 15)
    @ApiModelProperty(value = "task_id")
    private java.lang.String taskId;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String taskName;
	/**任务类型*/
	@Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型")
    private java.lang.String taskType;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String robotId;
	/**当前巡检点Id*/
	@Excel(name = "当前巡检点Id", width = 15)
    @ApiModelProperty(value = "当前巡检点Id")
    private java.lang.String patrolDeviceId;
	/**当前巡检点名称*/
	@Excel(name = "当前巡检点名称", width = 15)
    @ApiModelProperty(value = "当前巡检点名称")
    private java.lang.String patrolDeviceName;
	/**异常数量*/
	@Excel(name = "异常数量", width = 15)
    @ApiModelProperty(value = "异常数量")
    private java.lang.Integer errorDeviceSize;
	/**已完成数量*/
	@Excel(name = "已完成数量", width = 15)
    @ApiModelProperty(value = "已完成数量")
    private java.lang.Integer finishDeviceSize;
	/**完成进度*/
	@Excel(name = "完成进度", width = 15)
    @ApiModelProperty(value = "完成进度")
    private java.lang.Integer taskFinishPercentage;
	/**点位总数*/
	@Excel(name = "点位总数", width = 15)
    @ApiModelProperty(value = "点位总数")
    private java.lang.Integer totalDeviceSize;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
