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

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description: 巡检计划池
 * @Author: Mr.zhao
 * @Date: 2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("t_patrol_pool")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "patrol_pool对象", description = "巡检计划池")
public class PatrolPool {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 计划名称
	 */
	@Excel(name = "计划名称", width = 15)
	@ApiModelProperty(value = "计划名称")
	private String patrolName;


	/**
	 * 巡检系统
	 */
	@Excel(name = "巡检系统", width = 15)
	@ApiModelProperty(value = "巡检系统")
	private String systemType;


	/**
	 * 巡检系统名称
	 */
	@Excel(name = "巡检系统名称", width = 15)
	@ApiModelProperty(value = "巡检系统名称")
	private String systemTypeName;


	/**
	 * 编号		示例:X101.2109.001
	 */
	@Excel(name = "编号", width = 15)
	@ApiModelProperty(value = "编号		示例:X101.2109.001")
	private String code;

	/**
	 * 组织id
	 */
	@Excel(name = "组织id", width = 15)
	@ApiModelProperty(value = "组织id")
	private String organizationId;


	/**
	 * 站点id
	 */
	@Excel(name = "站点id", width = 15)
	@ApiModelProperty(value = "站点id")
	private Integer lineId;

	/**
	 * 站点名称
	 */
	@Excel(name = "站点名称", width = 15)
	@ApiModelProperty(value = "站点名称")
	private String lineName;


	/**
	 * 指派状态	0.未指派 1.已指派
	 */
	@Excel(name = "指派状态", width = 15)
	@ApiModelProperty(value = "指派状态	0.未指派 1.已指派")
	private Integer status;


	/**
	 * 巡检频次	1.一天1次 2.一周2次 3.一周1次
	 */
	@Excel(name = "巡检频次", width = 15)
	@ApiModelProperty(value = "巡检频次	1.一天1次 2.一周2次 3.一周1次")
	private Integer tactics;


	/**
	 * 巡检次数
	 */
	@Excel(name = "巡检次数", width = 15)
	@ApiModelProperty(value = "巡检次数")
	private Integer counts;


	/**
	 * 是否手动下发任务
	 */
	@Excel(name = "巡检次数", width = 15)
	@ApiModelProperty(value = "是否手动下发任务 1.是 0.否 2.按时间完成的周任务")
	private Integer type;

	/**
	 * 执行时间
	 */
	@Excel(name = "执行时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "执行时间")
	private LocalDateTime executionTime;

	/**
	 * 说明/备注
	 */
	@Excel(name = "说明/备注", width = 15)
	@ApiModelProperty(value = "说明/备注")
	private String note;

	/**
	 * 上一条记录的id, 周完成2次,未选择时间时,第二次必有值
	 */
	@ApiModelProperty(value = "上一级的id, type=2 hide=1 时,必有值")
	private Long supId;

	/**
	 * 是否隐藏 0:否 1:是
	 */
	@ApiModelProperty(value = "是否隐藏 0:否 1:是")
	private Integer hide;

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
	private Date createTime;

	/**
	 * 修改时间
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;


	public static final String ID = "id";

	public static final String PATROL_NAME = "patrol_name";

	public static final String ORGANIZATION_ID = "organization_id";

	public static final String STATUS = "status";

	public static final String COUNTS = "counts";

	public static final String TYPE = "type";

	public static final String DEL_FLAG = "del_flag";

	public static final String CREATE_BY = "create_by";

	public static final String UPDATE_BY = "update_by";

	public static final String CREATE_TIME = "create_time";

	public static final String UPDATE_TIME = "update_time";

	public static final String SYSTEM_TYPE = "system_type";

	public static final String SYSTEM_TYPE_NAME = "system_type_name";

	public static final String EXECUTION_TIME = "execution_time";

	public static final String NOTE = "note";

	public static final String CODE = "code";

}
