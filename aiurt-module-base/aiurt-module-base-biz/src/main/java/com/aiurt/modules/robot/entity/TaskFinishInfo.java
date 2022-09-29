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
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date:   2022-09-28
 * @Version: V1.0
 */
@Data
@TableName("task_finish_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="task_finish_info对象", description="task_finish_info")
public class TaskFinishInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String taskId;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String taskName;
	/**任务类型*/
	@Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型")
    private java.lang.String taskType;
	/**任务模板id*/
	@Excel(name = "任务模板id", width = 15)
    @ApiModelProperty(value = "任务模板id")
    private java.lang.String taskPathId;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private java.lang.String robotId;
	/**任务状态*/
	@Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态")
    private java.lang.String finishState;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
    /**处置人*/
    @Excel(name = "处置人", width = 15)
    @ApiModelProperty(value = "处置人")
    private java.lang.String handleUserId;
    /**处置时间*/
    @Excel(name = "处置时间", width = 15)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处置时间")
    private java.util.Date handleTime;
    /**处置说明*/
    @Excel(name = "处置说明", width = 15)
    @ApiModelProperty(value = "处置说明")
    private java.lang.String handleExplain;
    /**是否已经处置（0未处置1已处置）*/
    @Excel(name = "是否已经处置（0未处置1已处置）", width = 15)
    @ApiModelProperty(value = "是否已经处置（0未处置1已处置）")
    private java.lang.Integer isHandle;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
}
