package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: TreeParam
 * @author: Mr.zhao
 * @date: 2021/10/14 19:28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TreeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "巡检池id")
	private Long poolId;

	@ApiModelProperty(value = "此数据状态")
	private Integer flag;

	@ApiModelProperty(value = "名称")
	private String name;


}
