package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: PatrolTaskDetailParam
 * @author: Mr.zhao
 * @date: 2021/9/25 0:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolTaskDetailParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主表id")
	private Long id;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "巡检池id")
	private Long poolId;

	@ApiModelProperty(value = "名称")
	private String title;
}
