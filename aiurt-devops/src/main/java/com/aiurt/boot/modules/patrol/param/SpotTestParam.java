package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Mr.zhao
 * @date 2022/1/14 18:46
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SpotTestParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull(message = "id不能为空")
	@ApiModelProperty(value = "任务Id")
	private Long id;

	@NotBlank(message = "内容不能为空")
	@ApiModelProperty(value = "内容")
	private String content;
}
