package com.aiurt.boot.modules.apphome.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: AppHomeVO
 * @author: Mr.zhao
 * @date: 2021/11/28 19:47
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AppHomeVO implements Serializable {
	private static final long serialVersionUID = 1L;

	public AppHomeVO() {
		size = 0;
		overSize = 0;
	}


	@ApiModelProperty(value = "显示页面")
	private IPage<UserTask> page;

	/**
	 * 当前总数量
	 */
	@ApiModelProperty(value = "当前总数量")
	private Integer size;

	/**
	 * 已完结数量
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
