package com.aiurt.boot.modules.patrol.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: PatrolTaskVO
 * @author: Mr.zhao
 * @date: 2021/9/17 19:17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "巡检列表显示", description = "巡检列表显示")
public class PatrolTaskVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键id")
	private Long id;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "巡检表名称")
	private String name;

	@ApiModelProperty(value = "巡检系统")
	private String systemType;

	@ApiModelProperty(value = "班组id")
	private String organizationId;

	@ApiModelProperty(value = "班组名称")
	private String organizationName;

	@ApiModelProperty(value = "巡检人ids")
	private  String staffIds;

	@ApiModelProperty(value = "巡检人名称")
	private String staffName;

	@ApiModelProperty(value = "巡检频率: 1.一天1次 2.一周2次 3.一周1次")
	private String tactics;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "要求完成时间")
	private Date executionTime;

	@ApiModelProperty(value = "漏检任务处理状态: 0.否 1.是")
	private Integer ignoreStatus;

	@ApiModelProperty(value = "漏检任务处理信息")
	private String ignoreContent;

	@ApiModelProperty(value = "漏检任务时间")
	private String ignoreTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "提交时间")
	private Date submitTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "巡检要求完成时间")
	private Date createTime;

}
