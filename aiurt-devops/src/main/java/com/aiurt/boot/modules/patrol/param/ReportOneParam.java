package com.aiurt.boot.modules.patrol.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
	@NotNull(message = "巡检任务表id不能为空")
	private Long taskId;

	@ApiModelProperty(value = "任务项id")
	@NotNull(message = "任务项id不能为空")
	private Long contentId;

	@ApiModelProperty(value = "异常状态 0.文字项 1.无异常 2.异常")
	@NotNull(message = "状态不能为空")
	private Integer reportStatus;

	@ApiModelProperty(value = "保存状态 0.保存 1.提交")
	@NotNull(message = "状态不能为空")
	private Integer saveStatus;

	@ApiModelProperty(value = "填写项内容")
	private String note;

	@ApiModelProperty(value = "异常描述")
	private String unNote;

	@ApiModelProperty(value = "保留字段")
	private String content;

	@ApiModelProperty(value = "url地址集合")
	private List<String> urlList;

	private List<ReportOneParam> children;

}
