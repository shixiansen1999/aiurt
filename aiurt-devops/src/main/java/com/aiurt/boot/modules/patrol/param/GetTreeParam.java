package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: GetTreeParam
 * @author: Mr.zhao
 * @date: 2021/10/14 20:33
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class GetTreeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "计划池id")
	private Long poolId;

	@ApiModelProperty(value = "任务表id")
	private Long taskId;

	@ApiModelProperty(value = "父类id")
	private Long parentId;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "状态")
	private Integer flag;

	@ApiModelProperty(value = "提交状态")
	private Integer submitStatus;

	@ApiModelProperty(value = "计数")
	private Integer count;

	@ApiModelProperty(value = "名称")
	private String name;

}
