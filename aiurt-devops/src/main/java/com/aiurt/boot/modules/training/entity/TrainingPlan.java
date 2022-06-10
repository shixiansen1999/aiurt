package com.aiurt.boot.modules.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date: 2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("t_training_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "training_plan对象", description = "培训计划")
public class TrainingPlan {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

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
	 * 培训方式 数据字典配置
	 */
	@Dict(dicCode = "training_methods")
	@ApiModelProperty(value = "培训方式")
	private Integer trainingMethods;

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

	/**
	 * 课时（分钟）
	 */
	@ApiModelProperty(value = "课时（分钟）")
	private Integer classHour;

	/**
	 * 说明
	 */
	@ApiModelProperty(value = "说明")
	private String remarks;

	/**
	 * 删除状态 0-未删除 1-已删除
	 */
	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;


	/**
	 * 二维码
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "二维码")
	private String qrCode;

	/**
	 * 人员ids
	 */
	@NotNull(message = "人员不能为空")
	@Size(min = 1,message = "人员总数不能少于1")
	@TableField(exist = false)
	@ApiModelProperty(value = "人员ids,传数组")
	private List<String> userIds;

	/**
	 * 文件ids
	 */
	@TableField(exist = false)
	@ApiModelProperty(value = "文件ids,传数组")
	private List<Long> fileIds;

}
