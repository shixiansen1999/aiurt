package com.aiurt.modules.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
    private String id;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
	/**任务类型*/
	@Excel(name = "任务类型", width = 15)
    @ApiModelProperty(value = "任务类型")
    private String taskType;
	/**任务模板id*/
	@Excel(name = "任务模板id", width = 15)
    @ApiModelProperty(value = "任务模板id")
    private String taskPathId;
	/**机器人id*/
	@Excel(name = "机器人id", width = 15)
    @ApiModelProperty(value = "机器人id")
    private String robotId;
	/**任务状态 0：等待执行，1：已完成，2：故障未执行/中止，3：任务超期，4：挂牌未执行，5：正在执行，6：取消任务，7：已下发等待执行，8：暂停任务*/
	@Excel(name = "任务状态 0：等待执行，1：已完成，2：故障未执行/中止，3：任务超期，4：挂牌未执行，5：正在执行，6：取消任务，7：已下发等待执行，8：暂停任务", width = 15)
    @ApiModelProperty(value = "任务状态 0：等待执行，1：已完成，2：故障未执行/中止，3：任务超期，4：挂牌未执行，5：正在执行，6：取消任务，7：已下发等待执行，8：暂停任务")
    private String finishState;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    /**处置人*/
    @Excel(name = "处置人", width = 15)
    @ApiModelProperty(value = "处置人")
    private String handleUserId;
    /**处置时间*/
    @Excel(name = "处置时间", width = 15)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处置时间")
    private Date handleTime;
    /**处置说明*/
    @Excel(name = "处置说明", width = 15)
    @ApiModelProperty(value = "处置说明")
    private String handleExplain;
    /**是否已经处置（0未处置1已处置）*/
    @Excel(name = "是否已经处置（0未处置1已处置）", width = 15)
    @ApiModelProperty(value = "是否已经处置（0未处置1已处置）")
    private Integer isHandle;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
}
