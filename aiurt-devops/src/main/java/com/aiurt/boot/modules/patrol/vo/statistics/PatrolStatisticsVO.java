package com.aiurt.boot.modules.patrol.vo.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: PatrolStatisticsVO
 * @author: Mr.zhao
 * @date: 2021/11/19 15:36
 */
@ApiModel(value = "统计信息对象",description = "统计信息对象")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolStatisticsVO implements Serializable {

	public PatrolStatisticsVO(){
		this.title = new StatisticsTitleVO();
		this.teamList = new ArrayList<>();
		this.systemList = new ArrayList<>();
		this.list = new ArrayList<>();
	}

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "标题块型数据")
	private StatisticsTitleVO title;

	@ApiModelProperty(value = "班组对比数据")
	private List<SimpStatisticsVO> teamList;

	@ApiModelProperty(value = "系统对比数据")
	private List<SimpStatisticsVO> systemList;

	@ApiModelProperty(value = "列表数据")
	private List<StatisticsListVO> list;
}
