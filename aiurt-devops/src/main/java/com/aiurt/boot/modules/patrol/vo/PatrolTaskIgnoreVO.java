package com.aiurt.boot.modules.patrol.vo;

import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description: PatrolTaskIgnoreVO
 * @author: Mr.zhao
 * @date: 2021/11/5 15:52
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolTaskIgnoreVO extends PatrolTask implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "执行时间,最后时间")
	private LocalDateTime executionTime;

	@ApiModelProperty(value = "巡检池创建时间")
	private Date poolCreateTime;

}
