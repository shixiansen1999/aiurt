package com.aiurt.boot.modules.patrol.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description: TaskDetailVO
 * @author: Mr.zhao
 * @date: 2021/9/22 17:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TaskDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检表名称")
	private String title;

	@ApiModelProperty(value = "班组名称")
	private String organizationName;

	@ApiModelProperty(value = "巡检人")
	private String staffName;

	@ApiModelProperty(value = "提交时间")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitTime;

	@ApiModelProperty(value = "巡检项")
	private List<PatrolPoolContentTreeVO> list;

}
