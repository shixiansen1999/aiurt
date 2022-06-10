package com.aiurt.boot.modules.webHome.vo;

import com.aiurt.boot.modules.patrol.vo.statistics.SimpStatisticsVO;
import com.aiurt.boot.modules.sysFile.entity.SimpNameVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: PatrolHomeVO
 * @author: Mr.zhao
 * @date: 2021/11/19 18:42
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolHomeVO implements Serializable {

	public PatrolHomeVO(){
		allSize = 0;
		system = new ArrayList<>();
		teamList = new ArrayList<>();
		repaireAmount= 0;
	}


	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检总数量")
	private Integer allSize;


	@ApiModelProperty(value = "系统占比")
	private List<SimpNameVO> system;

	@ApiModelProperty(value = "班组巡检数对比")
	private List<SimpStatisticsVO> teamList;

	@ApiModelProperty(value = "检修数量")
	private Integer repaireAmount;


}
