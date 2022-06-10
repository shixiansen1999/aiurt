package com.aiurt.boot.modules.patrol.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: StatisticsVO
 * @author: Mr.zhao
 * @date: 2021/11/19 10:08
 */
@ApiModel(value = "统计-班组标题块vo", description = "统计-班组标题块vo")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StatisticsTitleVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public StatisticsTitleVO() {
		this.patrolSize = 0;
		this.dayPatrolSize = 0;
		this.weekPatrolSize = 0;

		this.notInspectedSize = 0;
		this.dayNotInspectedSize = 0;
		this.weekNotInspectedSize = 0;

		this.inspectedSize = 0;
		this.dayInspectedSize = 0;
		this.weekInspectedSize = 0;
	}



	@ApiModelProperty(value = "巡检总数")
	private Integer patrolSize;

	@ApiModelProperty(value = "日巡检总数")
	private Integer dayPatrolSize;

	@ApiModelProperty(value = "周巡检总数")
	private Integer weekPatrolSize;


	@ApiModelProperty(value = "未巡检总数")
	private Integer notInspectedSize;

	@ApiModelProperty(value = "日未巡检总数")
	private Integer dayNotInspectedSize;

	@ApiModelProperty(value = "周未巡检总数")
	private Integer weekNotInspectedSize;


	@ApiModelProperty(value = "已巡检总数")
	private Integer inspectedSize;

	@ApiModelProperty(value = "日已巡检总数")
	private Integer dayInspectedSize;

	@ApiModelProperty(value = "周已巡检总数")
	private Integer weekInspectedSize;


}
