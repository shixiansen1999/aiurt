package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @description: PoolAppointListParam
 * @author: Mr.zhao
 * @date: 2021/11/24 21:54
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PoolAppointListParam implements Serializable {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "巡检池id")
	@NotNull(message = "巡检池id不能为空")
	@Size(min = 1,message = "巡检池id不能少于1个")
	private List<Long> ids;

	@ApiModelProperty(value = "指派人员的集合")
	@NotNull(message = "人员数量不能少于1人")
	@Size(min = 1,message = "人员数量不能少于1人")
	private List<String> userIds;

	@ApiModelProperty(value = "是否为手动下发任务 0.否 1.是")
	@NotNull(message = "type")
	private Integer type;
}
