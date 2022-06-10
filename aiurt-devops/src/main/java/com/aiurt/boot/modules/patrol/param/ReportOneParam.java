package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @description: ReportOneParam
 * @author: Mr.zhao
 * @date: 2021/9/21 17:46
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ReportOneParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检任务表id")
	private Long id;

	@ApiModelProperty(value = "任务项id")
	@NotNull(message = "任务项id不能为空")
	private Long contentId;

	@ApiModelProperty(value = "异常状态 0.文字项 1.无异常 2.异常")
	@NotNull(message = "状态不能为空")
	private Integer status ;

	@ApiModelProperty(value = "保存状态 0.保存 1.提交")
	private Integer saveStatus;

	@ApiModelProperty(value = "备注/异常备注")
	@NotBlank(message = "备注不能为空")
	private String note;

	@ApiModelProperty(value = "url地址集合")
	private List<String> urlList;

}
