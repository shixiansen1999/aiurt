package com.aiurt.boot.modules.patrol.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: SImpDateVO
 * @author: Mr.zhao
 * @date: 2021/11/2 16:06
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SImpDateVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id ;

	@ApiModelProperty(value = "时间")
	private LocalDateTime time;

}
