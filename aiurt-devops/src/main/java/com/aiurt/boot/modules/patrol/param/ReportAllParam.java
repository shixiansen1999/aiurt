package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: ReportAllParam
 * @author: Mr.zhao
 * @date: 2021/9/21 18:25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ReportAllParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检人员任务表id")
	@NotNull(message = "任务表id不能为空")
	private Long id;

	@ApiModelProperty(value = "巡检项每一项的list集合,不用传taskId")
	private List<ReportOneParam> list;

	@ApiModelProperty(value = "保存状态 0.保存 1.提交")
	private Integer status;



}
