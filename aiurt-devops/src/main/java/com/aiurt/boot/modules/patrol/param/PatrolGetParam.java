package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: PatrolGetParam
 * @author: Mr.zhao
 * @date: 2021/9/28 14:06
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolGetParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id")
	private String userId;

	//@ApiModelProperty(value = "时间")
	//private

}
