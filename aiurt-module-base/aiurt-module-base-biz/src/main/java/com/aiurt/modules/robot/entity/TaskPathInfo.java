package com.aiurt.modules.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: task_path_info
 * @Author: aiurt
 * @Date:   2022-09-26
 * @Version: V1.0
 */
@Data
@TableName("task_path_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="task_path_info对象", description="task_path_info")
@Builder
public class TaskPathInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**任务模板id*/
	@Excel(name = "任务模板id", width = 15)
    @ApiModelProperty(value = "任务模板id")
    private java.lang.String taskPathId;
	/**任务模板名称*/
	@Excel(name = "任务模板名称", width = 15)
    @ApiModelProperty(value = "任务模板名称")
    private java.lang.String taskPathName;
	/**任务模板类型*/
	@Excel(name = "任务模板类型", width = 15)
    @ApiModelProperty(value = "任务模板类型")
    private java.lang.String taskPathType;
	/**完成动作(0 自动充电，1 原地待命)*/
	@Excel(name = "完成动作(0 自动充电，1 原地待命)", width = 15)
    @ApiModelProperty(value = "完成动作(0 自动充电，1 原地待命)")
    private java.lang.Integer finishAction;
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
