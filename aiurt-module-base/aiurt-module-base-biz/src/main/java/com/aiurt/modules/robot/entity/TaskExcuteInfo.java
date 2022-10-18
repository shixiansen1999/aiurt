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
 * @Description: task_excute_info
 * @Author: aiurt
 * @Date:   2022-09-29
 * @Version: V1.0
 */
@Data
@TableName("task_excute_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="task_excute_info对象", description="task_excute_info")
public class TaskExcuteInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
	/**任务模板id*/
	@Excel(name = "任务模板id", width = 15)
    @ApiModelProperty(value = "任务模板id")
    private String taskPathId;
	/**巡检结果id*/
	@Excel(name = "巡检结果id", width = 15)
    @ApiModelProperty(value = "巡检结果id")
    private String targetId;
	/**巡检点位id*/
	@Excel(name = "巡检点位id", width = 15)
    @ApiModelProperty(value = "巡检点位id")
    private String pointId;
	/**执行结果值*/
	@Excel(name = "执行结果值", width = 15)
    @ApiModelProperty(value = "执行结果值")
    private String excuteValue;
	/**执行结果状态:正常、异常*/
	@Excel(name = "执行结果状态:正常、异常", width = 15)
    @ApiModelProperty(value = "执行结果状态:正常、异常")
    private String excuteState;
	/**执行结果描述:一般告警*/
	@Excel(name = "执行结果描述:一般告警", width = 15)
    @ApiModelProperty(value = "执行结果描述:一般告警")
    private String excuteDesc;
	/**高清图片(jpg)*/
	@Excel(name = "高清图片(jpg)", width = 15)
    @ApiModelProperty(value = "高清图片(jpg)")
    private String hdPicture;
	/**红外图片相对路径（bmp）*/
    @Excel(name = "红外图片相对路径（bmp）", width = 15)
    @ApiModelProperty(value = "红外图片相对路径（bmp）")
    private String infraredPicture;
	/**执行时间*/
	@Excel(name = "执行时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "执行时间")
    private Date excuteTime;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private String device;
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
