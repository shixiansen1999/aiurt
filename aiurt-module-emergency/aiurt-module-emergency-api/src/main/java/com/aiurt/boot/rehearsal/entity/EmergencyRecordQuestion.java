package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.aiurt.boot.rehearsal.constant.EmergencyDictConstant;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_record_question
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_record_question")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_record_question对象", description="emergency_record_question")
public class EmergencyRecordQuestion extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**责任人ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "责任人ID")
    private java.lang.String id;
    /**实施记录ID*/
    @Excel(name = "实施记录ID", width = 15)
    @ApiModelProperty(value = "实施记录ID")
    private java.lang.String recordId;
	/**问题分类(1问题、2隐患)*/
	@Excel(name = "问题分类(1问题、2隐患)", width = 15)
    @ApiModelProperty(value = "问题分类(1问题、2隐患)")
    @Dict(dicCode = EmergencyDictConstant.QUESTION_CATEGORY)
    private java.lang.Integer category;
    /**问题分类字典名称*/
    @Excel(name = "问题分类字典名称", width = 15)
    @ApiModelProperty(value = "问题分类字典名称")
    @TableField(exist = false)
    private java.lang.String categoryName;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private java.lang.String description;
	/**责任部门编码*/
	@Excel(name = "责任部门编码", width = 15)
    @ApiModelProperty(value = "责任部门编码")
    private java.lang.String orgCode;
	/**责任部门负责人ID*/
	@Excel(name = "责任部门负责人ID", width = 15)
    @ApiModelProperty(value = "责任部门负责人ID")
    private java.lang.String orgUserId;
	/**责任人ID*/
	@Excel(name = "责任人ID", width = 15)
    @ApiModelProperty(value = "责任人ID")
    private java.lang.String userId;
	/**解决期限*/
	@Excel(name = "解决期限", width = 15)
    @ApiModelProperty(value = "解决期限")
    private java.lang.String deadline;
	/**问题状态(1待处理、2处理中、3已处理)*/
	@Excel(name = "问题状态(1待处理、2处理中、3已处理)", width = 15)
    @ApiModelProperty(value = "问题状态(1待处理、2处理中、3已处理)")
    @Dict(dicCode = EmergencyDictConstant.QUESTION_STATUS)
    private java.lang.Integer status;
    /**问题状态字典名称*/
    @Excel(name = "问题状态字典名称", width = 15)
    @ApiModelProperty(value = "问题状态字典名称")
    @TableField(exist = false)
    private java.lang.String statusName;
	/**处理方式*/
	@Excel(name = "处理方式", width = 15)
    @ApiModelProperty(value = "处理方式")
    private java.lang.String processMode;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
