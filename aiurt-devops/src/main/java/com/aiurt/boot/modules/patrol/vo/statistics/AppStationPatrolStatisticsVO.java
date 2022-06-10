package com.aiurt.boot.modules.patrol.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: SimpStatisticsVO
 * @author: Mr.zhao
 * @date: 2021/11/19 10:25
 */
@ApiModel(value = "统计-APP站点巡检统计数据VO", description = "统计-APP站点巡检统计数据VO")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AppStationPatrolStatisticsVO implements Serializable {

	public AppStationPatrolStatisticsVO() {
		this.name = null;
		this.id = 0l;
		this.num = 0;
		this.completeNum = 0;
		this.ignoreNum = 0;
	}

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "站点id")
	private Long id;

	@ApiModelProperty(value = "计划数量")
	private Integer num;

	@ApiModelProperty(value = "已完成数量")
	private Integer completeNum;

	@ApiModelProperty(value = "漏检数量")
	private Integer ignoreNum;


}
