package com.aiurt.boot.common.api.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: PageVO
 * @author: Mr.zhao
 * @date: 2021/9/18 12:08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "页码")
	private Integer pageNo;
	@ApiModelProperty(value = "每页显示条数")
	private Integer pageSize;




}
