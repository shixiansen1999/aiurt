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
@ApiModel(value = "统计-对比数据VO", description = "统计-对比数据VO")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SimpStatisticsVO implements Serializable {

	public SimpStatisticsVO() {
		this.name = null;
		this.undone = 0;
		this.completed = 0;
	}

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "名称")
	private String name;

	@ApiModelProperty(value = "未完成数量")
	private Integer undone;

	@ApiModelProperty(value = "已完成数量")
	private Integer completed;
}
