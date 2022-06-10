package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: PatrolAppHomeParam
 * @author: Mr.zhao
 * @date: 2021/10/1 14:02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolAppHomeParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id")
	private String userId;

	@ApiModelProperty(value = "用户id")
	private Integer type;

	@ApiModelProperty(value = "开始时间")
	private LocalDateTime startTime;

	@ApiModelProperty(value = "结束时间")
	private LocalDateTime endTime;

	@ApiModelProperty(value = "关键字")
	private String keyName;


}
