package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: ReportSignParam
 * @author: Mr.zhao
 * @date: 2021/9/29 1:31
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ReportSignParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "taskId")
	private Long id;

	@ApiModelProperty(value = "url")
	private String url;
}
