package com.aiurt.modules.training.param;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: PlanUserParam
 * @Author: hlq
 * @Date: 2023-06-06
 * @Version: V1.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PlanUserParam implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 计划名称
	 */
	@ApiModelProperty(value = "计划名称")
	private String name;
	
	/**
	 * 主讲人
	 */
	@ApiModelProperty(value = "主讲人")
	private String presenter;
	
	/**
	 * 培训类型 数据字典配置
	 */
	@Dict(dicCode = "training_type")
	@ApiModelProperty(value = "培训类型")
	private Integer trainingType;
	
	/**
	 * 培训地点
	 */
	@ApiModelProperty(value = "培训地点")
	private String address;
	
	/**
	 * 开始日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "开始日期")
	private Date startDate;
	
	/**
	 * 结束日期
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "结束日期")
	private Date endDate;
	
	
	@ApiModelProperty(value = "用户id")
	private String userId;
}
