package com.aiurt.boot.modules.apphome.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description: UserTask
 * @author: Mr.zhao
 * @date: 2021/11/25 15:45
 */

/**
 * 人员事务表
 *
 * @author zhaojy
 * @date 2021/11/25
 */
@ApiModel(value = "人员事务表")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_user_task")
public class UserTask {
	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 人员id
	 */
	@TableField(value = "user_id")
	@ApiModelProperty(value = "人员id")
	private String userId;

	/**
	 * 人员名称
	 */
	@TableField(value = "real_name")
	@ApiModelProperty(value = "人员名称")
	private String realName;

	/**
	 * 类型	 1.巡检 2.检修 3.故障 4.工作日志
	 */
	@TableField(value = "`type`")
	@ApiModelProperty(value = "类型	 1.巡检 2.检修 3.故障 4.工作日志")
	private Integer type;

	/**
	 * 回调code	code与id不能同时为空
	 */
	@TableField(value = "record_code")
	@ApiModelProperty(value = "回调code	code与id不能同时为空")
	private String recordCode;

	/**
	 * 回调id
	 */
	@TableField(value = "record_id")
	@ApiModelProperty(value = "回调id")
	private Long recordId;

	/**
	 * 任务编号
	 */
	@TableField(value = "work_code")
	@ApiModelProperty(value = "任务编号")
	private String workCode;

	/**
	 * 发生时间: 故障专用字段
	 */
	@ApiModelProperty(value = "发生时间: 故障专用字段")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date productionTime;

	/**
	 * 标题
	 */
	@TableField(value = "title")
	@ApiModelProperty(value = "标题")
	private String title;

	/**
	 * 内容字段	(保留字段)
	 */
	@TableField(value = "content")
	@ApiModelProperty(value = "内容字段	(保留字段)")
	private String content;

	/**
	 * 备注字段	(保留字段)
	 */
	@TableField(value = "note")
	@ApiModelProperty(value = "备注字段	(保留字段)")
	private String note;

	/**
	 * 显示级别	排序字段
	 * 1.工作日志未上报
	 * 2.检修,巡检
	 * 3-4.普通故障(自检与保修)
	 * 5-6.重大故障(自检与保修)
	 */
	@TableField(value = "`level`")
	@ApiModelProperty(value = "显示级别	排序字段, 1.工作日志未上报 2.检修,巡检 3-4.普通故障(自检与保修) 5-6.重大故障(自检与保修)")
	private Integer level;

	/**
	 * 状态	0.待办 1.已完成
	 */
	@TableField(value = "`status`")
	@ApiModelProperty(value = "状态	0.待办 1.已完成")
	private Integer status;

	/**
	 * 工作时间
	 */
	@TableField(value = "`work_time`")
	@ApiModelProperty(value = "工作时间")
	private LocalDate workTime;

	/**
	 * 工作完成时间
	 */
	@TableField(value = "complete_time")
	@ApiModelProperty(value = "工作完成时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime completeTime;

	/**
	 * 删除状态	0.未删除 1.已删除
	 */
	@TableField(value = "del_flag")
	@ApiModelProperty(value = "删除状态	0.未删除 1.已删除")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@TableField(value = "create_by")
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@TableField(value = "update_by")
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@TableField(value = "update_time")
	@ApiModelProperty(value = "修改时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
}
