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
    private java.lang.String id;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String taskId;
	/**任务模板id*/
	@Excel(name = "任务模板id", width = 15)
    @ApiModelProperty(value = "任务模板id")
    private java.lang.String taskPathId;
	/**巡检结果id*/
	@Excel(name = "巡检结果id", width = 15)
    @ApiModelProperty(value = "巡检结果id")
    private java.lang.String targetId;
	/**巡检点位id*/
	@Excel(name = "巡检点位id", width = 15)
    @ApiModelProperty(value = "巡检点位id")
    private java.lang.String pointId;
	/**执行结果值*/
	@Excel(name = "执行结果值", width = 15)
    @ApiModelProperty(value = "执行结果值")
    private java.lang.String excuteValue;
	/**执行结果状态:正常、异常*/
	@Excel(name = "执行结果状态:正常、异常", width = 15)
    @ApiModelProperty(value = "执行结果状态:正常、异常")
    private java.lang.String excuteState;
	/**执行结果描述:一般告警*/
	@Excel(name = "执行结果描述:一般告警", width = 15)
    @ApiModelProperty(value = "执行结果描述:一般告警")
    private java.lang.String excuteDesc;
	/**高清图片(jpg)*/
	@Excel(name = "高清图片(jpg)", width = 15)
    @ApiModelProperty(value = "高清图片(jpg)")
    private java.lang.String hdPicture;
	/**执行时间*/
	@Excel(name = "执行时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "执行时间")
    private java.util.Date excuteTime;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private java.lang.String device;
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
