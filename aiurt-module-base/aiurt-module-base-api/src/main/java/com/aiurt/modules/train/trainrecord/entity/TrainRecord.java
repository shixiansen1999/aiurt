package com.aiurt.modules.train.trainrecord.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: train_record
 * @Author: aiurt
 * @Date:   2023-06-25
 * @Version: V1.0
 */
@Data
@TableName("train_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="train_record对象", description="培训记录")
public class TrainRecord extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**培训档案id*/
    @ApiModelProperty(value = "培训档案id")
    private String trainArchiveId;
	/**培训时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "培训时间")
    private Date trainTime;
	/**培训分级*/
    @ApiModelProperty(value = "培训分级")
    @Dict(dicCode = "training_classification")
    private Integer taskGrade;
	/**培训课时*/
	@Excel(name = "课时", width = 15)
    @ApiModelProperty(value = "培训课时")
    private Integer hour;
	/**考核成绩*/
	@Excel(name = "考核成绩", width = 15)
    @ApiModelProperty(value = "考核成绩")
    private String checkGrade;
	/**是否为计划内，0是，1不是*/
    @Dict(dicCode = "is_annual_plan")
    @ApiModelProperty(value = "是否为计划内，0是，1不是")
    private Integer isAnnualPlan;
	/**记录编号*/
	@Excel(name = "记录编号", width = 15)
    @ApiModelProperty(value = "记录编号")
    private String taskCode;
	/**删除标志，0是未删除，1是删除*/
    @ApiModelProperty(value = "删除标志，0是未删除，1是删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    /**培训分级名称*/
    @Excel(name = "培训分级", width = 15)
    @ApiModelProperty(value = "培训分级名称")
    @TableField(exist = false)
    private String taskGradeName;
    /**是否为计划内*/
    @Excel(name = "是否为计划内", width = 15)
    @ApiModelProperty(value = "是否计划内")
    @TableField(exist = false)
    private String isAnnualPlanName;
    /**培训内容*/
    @Excel(name = "培训内容", width = 15)
    @ApiModelProperty(value = "培训内容")
    private String trainContent;
    /**培训内容*/
    @Excel(name = "培训任务Id", width = 15)
    @ApiModelProperty(value = "培训任务Id")
    private String trainTaskId;


}
