package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: IgnoreTaskParam
 * @author: Mr.zhao
 * @date: 2021/9/21 16:06
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class IgnoreTaskParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检人员任务表id")
	@NotNull(message = "任务表id不能为空")
	private Long id;

	@ApiModelProperty(value = "漏检处理信息")
	@NotBlank(message = "处理信息不能未空")
	private String content;


}
