package com.aiurt.boot.modules.patrol.param;

import com.swsc.copsms.modules.patrol.entity.Patrol;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

/**
 * @description: PatrolPageParam
 * @author: Mr.zhao
 * @date: 2021/9/14 22:51
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class PatrolPageParam extends Patrol implements Serializable {

	@ApiModelProperty(value = "创建人")
	private String createByName;

	@ApiModelProperty(value = "后端处理")
	private List<String> userIds;

}
