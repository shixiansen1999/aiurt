package com.aiurt.boot.modules.apphome.vo;

import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: HomeVO
 * @author: Mr.zhao
 * @date: 2021/11/11 11:20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HomeVO implements Serializable {

	@ApiModelProperty(value = "巡检集合")
	private List<PatrolAppTaskVO> patrolList;

	@ApiModelProperty(value = "检修集合")
	private List<RepairTask> repairTaskList;

	@ApiModelProperty(value = "故障集合")
	private List<FaultHomeVO> faultList;

	@ApiModelProperty(value = "检修集合")
	private List<WorkLogVO> workLogList;

	@ApiModelProperty(value = "当前总数量")
	private Integer size;

	/**
	 * 	已完结数量
	 */
	@ApiModelProperty(value = "已完结总数量")
	private Integer overSize;

	/**
	 * 未完成数据集合
	 */
	@ApiModelProperty(value = "未完成数据集合")
	private SimpSizeVO pendingVO;

	/**
	 * 已完成的数据集合
	 */
	@ApiModelProperty(value = "已完成的数据集合")
	private SimpSizeVO completedVO;

}
