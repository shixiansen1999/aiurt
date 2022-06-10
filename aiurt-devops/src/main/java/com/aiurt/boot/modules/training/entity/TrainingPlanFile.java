package com.aiurt.boot.modules.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 课件库
 *
 * @description: TrainingPlanFile
 * @author: Mr.zhao
 * @date: 2021/11/28 16:49
 */
@ApiModel(value = "课件库")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "t_training_plan_file")
public class TrainingPlanFile {
	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 培训计划id
	 */
	//@TableField(value = "plan_id")
	@ApiModelProperty(value = "培训计划id")
	private Long planId;

	/**
	 * 文件id
	 */
	@TableField(value = "file_id")
	@ApiModelProperty(value = "文件id")
	private Long fileId;

	/**
	 * 删除状态 0-未删除 1-已删除
	 */
	@TableField(value = "del_flag")
	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@TableField(value = "create_by")
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@TableField(value = "update_by")
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@TableField(value = "update_time")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
}
