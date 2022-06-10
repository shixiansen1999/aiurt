package com.aiurt.boot.modules.training.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 培训计划对象
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("t_training_plan_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "training_plan_user对象", description = "培训计划对象")
public class TrainingPlanUser {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 培训计划id
	 */
	@ApiModelProperty(value = "培训计划id")
	private Long planId;

	/**
	 * 人员id
	 */
	@ApiModelProperty(value = "人员id")
	private String userId;

	/**
	 * 人员名称
	 */
	@ApiModelProperty(value = "人员名称")
	private String realName;

	/**
	 * 签到状态 0-未签到 1-已签到
	 */
	@ApiModelProperty(value = "签到状态 0-未签到 1-已签到")
	private Integer signStatus;

	/**
	 * 签到时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "签到时间")
	private Date signTime;

	/**
	 * 证书
	 */
	@Excel(name = "证书", width = 15)
	@ApiModelProperty(value = "证书")
	private String certificateUrl;

	/**
	 * 删除状态 0-未删除 1-已删除
	 */
	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
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
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;


	/**
	 * 部门名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "部门名称")
	private String teamName;


	/**
	 * 职位名称
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "职位名称")
	private String roleName;
}
