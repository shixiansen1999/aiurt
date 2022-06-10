package com.aiurt.boot.modules.training.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: PlanUserVO
 * @author: Mr.zhao
 * @date: 2021/11/29 17:27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PlanUserVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 计划名称
	 */
	@ApiModelProperty(value = "人员表id")
	private Long id;

	/**
	 * 计划名称
	 */
	@ApiModelProperty(value = "计划id")
	private Long planId;

	/**
	 * 计划名称
	 */
	@ApiModelProperty(value = "计划名称")
	private String name;


	/**
	 * 培训类型 数据字典配置
	 */
	@Dict(dicCode = "training_type")
	@ApiModelProperty(value = "培训类型")
	private Integer trainingType;

	/**
	 * 开始日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "开始日期")
	private Date startDate;

	/**
	 * 结束日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "结束日期")
	private Date endDate;


	/**
	 * 培训地点
	 */
	@ApiModelProperty(value = "培训地点")
	private String address;


	/**
	 * 课时（分钟）
	 */
	@ApiModelProperty(value = "课时（分钟）")
	private Integer classHour;

	/**
	 * 签到时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "签到时间")
	private Date signTime;

}
