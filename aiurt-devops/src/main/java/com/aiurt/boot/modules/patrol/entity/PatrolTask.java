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

import java.util.Date;

/**
 * @Description: 巡检人员任务
 * @Author: Mr.zhao
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("t_patrol_task")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "patrol_task对象", description = "巡检人员任务")
public class PatrolTask {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 巡检计划池id		patrol_pool.id
	 */
	@Excel(name = "巡检计划池id", width = 15)
	@ApiModelProperty(value = "巡检计划池id")
	private Long patrolPoolId;

	/**
	 * 编号		示例:X101.2109.001
	 */
	@Excel(name = "编号", width = 15)
	@ApiModelProperty(value = "编号		示例:X101.2109.001")
	private String code;

	/**
	 * 巡检次数
	 */
	@Excel(name = "巡检次数", width = 15)
	@ApiModelProperty(value = "巡检次数")
	private Integer counts;

	/**
	 * 巡检状态	0.未巡检 1.已巡检
	 */
	@Excel(name = "巡检状态", width = 15)
	@ApiModelProperty(value = "巡检状态	0.未巡检 1.已巡检")
	private Integer status;

	/**
	 * 巡检人		巡检人ids
	 */
	@Excel(name = "巡检人", width = 15)
	@ApiModelProperty(value = "巡检人		巡检人ids")
	private String staffIds;

	/**
	 * 巡检人		巡检人ids
	 */
	@Excel(name = "巡检人", width = 15)
	@ApiModelProperty(value = "巡检人		巡检人ids")
	private String staffName;


	@ApiModelProperty(value = "异常状态")
	@Excel(name = "异常状态", width = 10)
	private Integer warningStatus;

	/**
	 * 故障状态		0.无故障 1.有故障
	 */
	@Excel(name = "故障状态", width = 15)
	@ApiModelProperty(value = "故障状态		0.无故障 1.有故障")
	private Integer errorStatus;

	/**
	 * 报告时间
	 */
	@Excel(name = "报告时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "报告时间")
	private Date submitTime;

	/**
	 * 漏检状态	0.否 1.是
	 */
	@Excel(name = "漏检状态	0.否 1.是", width = 15)
	@ApiModelProperty(value = "漏检状态	0.否 1.是")
	private Integer ignoreStatus;

	/**
	 * 漏检处理信息		漏检状态为1时,无值置状态为未处理
	 */
	@Excel(name = "漏检处理信息		漏检状态为1时,无值置状态为未处理", width = 15)
	@ApiModelProperty(value = "漏检处理信息		漏检状态为1时,无值置状态为未处理")
	private String ignoreContent;

	/**
	 * 漏检时间		值为时间的固定字符串形式
	 */
	@Excel(name = "漏检时间		值为时间的固定字符串形式", width = 15)
	@ApiModelProperty(value = "漏检时间		值为时间的固定字符串形式")
	private String ignoreTime;


	/**
	 * 抽查信息
	 */
	@Excel(name = "抽查信息", width = 15)
	@ApiModelProperty(value = "抽查信息")
	private String spotTest;

	/**
	 * 抽查人id
	 */
	@Excel(name = "抽查人", width = 15)
	@ApiModelProperty(value = "抽查人id")
	private String spotTestUser;

	/**
	 * 删除状态	0.未删除 1已删除
	 */
	@Excel(name = "删除状态	0.未删除 1已删除", width = 15)
	@ApiModelProperty(value = "删除状态	0.未删除 1已删除")
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

	///**
	// * 是否手动下发任务
	// */
	//@Excel(name = "是否手动下发任务", width = 15)
	//@ApiModelProperty(value = "是否手动下发任务	0.否 1.是")
	//private Integer type;

	/**
	 * 技术员抽查内容
	 */
	@Excel(name = "技术员抽查内容", width = 15)
	@ApiModelProperty(value = "技术员抽查内容")
	private String spotTestTechnician;

	/**
	 * 抽查人id
	 */
	@Excel(name = "抽查技术员Id", width = 15)
	@ApiModelProperty(value = "抽查技术员Id")
	private String spotTestTechnicianId;


	/**
	 * 提交人id
	 */
	@ApiModelProperty(value = "提交人id")
	private String submitUserId;

	/**
	 * 提交人
	 */
//    @Excel(name = "提交人", width = 15)
	@ApiModelProperty(value = "提交人")
	private String submitUserName;
}
