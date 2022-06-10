package com.aiurt.boot.modules.apphome.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: PatrolAppTaskVO
 * @author: Mr.zhao
 * @date: 2021/11/11 12:09
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolAppTaskVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "人员名称")
	private String staffName;

	@ApiModelProperty(value = "状态 0:待指派 1.带巡检 2.漏检待处理")
	private Integer status;

	@ApiModelProperty(value = "开始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;



}
