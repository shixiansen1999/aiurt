package com.aiurt.boot.modules.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: UserChildrenVO
 * @author: Mr.zhao
 * @date: 2021/11/22 15:46
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class UserChildrenVO implements Serializable {

	@ApiModelProperty(value = "主键id")
	private String key;

	@ApiModelProperty(value = "名称")
	private String title;

	@ApiModelProperty(value = "是否为人员, 1:人员 0:班组")
	private Integer userFlag;

}
