package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: UrlParam
 * @author: Mr.zhao
 * @date: 2021/9/28 23:02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UrlParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "库名称")
	private String name;

	@ApiModelProperty(value = "父级id")
	private Long id;
}
