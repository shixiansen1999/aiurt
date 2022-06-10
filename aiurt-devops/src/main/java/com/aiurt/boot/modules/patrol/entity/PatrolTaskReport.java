package com.aiurt.boot.modules.patrol.entity;

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

/**
 * @Description: 巡检人员巡检项报告表
 * @Author: Mr.zhao
 * @Date: 2021-09-21
 * @Version: V1.0
 */
@Data
@TableName("t_patrol_task_report")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "patrol_task_report对象", description = "巡检人员巡检项报告表")
public class PatrolTaskReport {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 巡检人员任务表id		patrol_task.id
	 */
	@Excel(name = "巡检人员任务表id		patrol_task.id", width = 15)
	@ApiModelProperty(value = "巡检人员任务表id		patrol_task.id")
	private Long patrolTaskId;

	/**
	 * 巡检项id
	 */
	@Excel(name = "巡检项id", width = 15)
	@ApiModelProperty(value = "巡检项id")
	private Long patrolPoolContentId;

	/**
	 * 异常状态		0.文字项 1.正常 2.异常
	 */
	@Excel(name = "异常状态		0.文字项 1.正常 2.异常", width = 15)
	@ApiModelProperty(value = "异常状态		0.文字项 1.正常 2.异常 3.已生成故障登记")
	private Integer status;

	/**
	 * 故障登记code
	 */
	@Excel(name = "故障登记code", width = 15)
	@ApiModelProperty(value = "故障登记code")
	private String code;

	/**
	 * 填写项信息
	 */
	@Excel(name = "填写项信息", width = 15)
	@ApiModelProperty(value = "填写项信息")
	private String note;

	/**
	 * 异常描述
	 */
	@Excel(name = "异常描述", width = 15)
	@ApiModelProperty(value = "异常描述")
	private String unNote;


	@ApiModelProperty(value = "保留字段")
	private String content;

	/**
	 * 保存状态		0.保存 1.提交
	 */
	@Excel(name = "保存状态		0.保存 1.提交", width = 15)
	@ApiModelProperty(value = "保存状态		0.保存 1.提交")
	private Integer saveStatus;


	/**
	 * 删除状态
	 */
	@Excel(name = "删除状态", width = 15)
	@ApiModelProperty(value = "删除状态")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;

	/**
	 * 修改时间
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;


	public static final String ID = "id";
	public static final String PATROL_TASK_ID = "patrol_task_id";
	public static final String PATROL_POOL_CONTENT_ID = "patrol_pool_content_id";
	public static final String STATUS = "status";
	public static final String NOTE = "note";
	public static final String DEL_FLAG = "del_flag";
	public static final String CREATE_BY = "create_by";
	public static final String UPDATE_BY = "update_by";
	public static final String CREATE_TIME = "create_time";
	public static final String UPDATE_TIME = "update_time";
	public static final String CODE = "code";


}
