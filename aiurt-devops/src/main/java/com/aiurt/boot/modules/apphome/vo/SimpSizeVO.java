package com.aiurt.boot.modules.apphome.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: SimpSizeVO
 * @author: Mr.zhao
 * @date: 2021/11/12 13:44
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SimpSizeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public SimpSizeVO(){
		patrolSize = 0;
		repairSize = 0;
		faultSize = 0;
	}

	@ApiModelProperty(value = "巡检总数量")
	private Integer patrolSize;

	@ApiModelProperty(value = "检修总数量")
	private Integer repairSize;

	@ApiModelProperty(value = "故障总数量")
	private Integer faultSize;

}
