package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: TaskAddParam
 * @author: Mr.zhao
 * @date: 2021/9/18 16:34
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "手动下发任务请求", description = "手动下发任务请求")
public class TaskAddParam  implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "班组id")
	@NotNull(message = "班组id不能为空")
	private List<String> organizationIds;

	@ApiModelProperty(value = "巡检表id")
	@NotNull(message = "巡检表id不能为空")
	private List<Long>  patrolIds;

	@ApiModelProperty(value = "执行时间")
	@NotNull(message = "执行时间不能为空")
	private Date time;

	@ApiModelProperty(value = "备注")
	@NotNull(message = "备注不能为空")
	private String note;

}
