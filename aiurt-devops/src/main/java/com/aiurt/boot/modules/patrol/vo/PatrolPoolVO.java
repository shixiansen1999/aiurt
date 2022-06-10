package com.aiurt.boot.modules.patrol.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: PatrolPoolVO
 * @author: Mr.zhao
 * @date: 2021/9/16 22:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "巡检池列表显示", description = "巡检池列表显示")
public class PatrolPoolVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id")
	private Long id;

	@ApiModelProperty(value = "标题名称")
	private String patrolName;

	@ApiModelProperty(value = "巡检系统")
	private String systemType;

	@ApiModelProperty(value = "组织id")
	private String organizationId;

	@ApiModelProperty(value = "指派状态 0.未指派 1.已指派")
	private String poolStatus;

	@ApiModelProperty(value = "巡检频率   1.一天1次 2.一周2次 3.一周1次")
	private String tactics;

	@ApiModelProperty(value = "巡检人ids")
	private String staffIds;

	@ApiModelProperty(value = "巡检状态 0.未巡检 1.已巡检")
	private String taskStatus;

	@ApiModelProperty(value = "巡检次数")
	private String counts;

	@ApiModelProperty(value = "巡检人名称")
	private String staffName;

	@ApiModelProperty(value = "组织名称")
	private String organizationName;


	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "执行时间")
	private Date executionTime;

	@ApiModelProperty(value = "备注")
	private String note;
}
