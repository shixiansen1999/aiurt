package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: OneTreeParam
 * @author: Mr.zhao
 * @date: 2021/11/10 17:44
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class OneTreeParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检池id")
	private Long poolId;

	@ApiModelProperty(value = "巡检任务id")
	private Long taskId;

	@ApiModelProperty(value = "巡检项id")
	@NotNull(message = "巡检项id不能为空")
	private Long typeId;
}
