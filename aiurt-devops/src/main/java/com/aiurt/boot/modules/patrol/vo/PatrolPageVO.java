package com.aiurt.boot.modules.patrol.vo;

import com.aiurt.boot.modules.patrol.entity.Patrol;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: PatrolPageVO
 * @author: Mr.zhao
 * @date: 2021/9/26 14:47
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolPageVO extends Patrol implements Serializable {

	private static final long serialVersionUID =1L;

	@ApiModelProperty(value = "巡检系统")
	private String systemTypeName;

	@ApiModelProperty(value = "车站名称")
	private String organizationName;


}
