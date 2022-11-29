package com.aiurt.boot.rehearsal.entity;

import java.io.Serializable;

import com.aiurt.boot.constant.EmergencyDictConstant;
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
 * @Description: 应急演练年计划实体对象
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_rehearsal_year")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="应急演练年计划实体对象", description="应急演练年计划实体对象")
public class EmergencyRehearsalYear extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**计划编号*/
	@Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    @TableField(value = "`code`")
    private java.lang.String code;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
    private java.lang.String name;
	/**所属年份格式：yyyy*/
	@Excel(name = "所属年份", width = 15, format = "yyyy")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy")
    @DateTimeFormat(pattern="yyyy")
    @ApiModelProperty(value = "所属年份格式：yyyy")
    @TableField(value = "`year`")
    private java.util.Date year;
	/**编制人ID*/
	@Excel(name = "编制人ID", width = 15)
    @ApiModelProperty(value = "编制人ID")
    @Dict(dictTable = "sys_user", dicCode = "id", dicText = "realname")
    private java.lang.String userId;
	/**编制部门编码*/
	@Excel(name = "编制部门编码", width = 15)
    @ApiModelProperty(value = "编制部门编码")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    private java.lang.String orgCode;
	/**编制日期*/
	@Excel(name = "编制日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "编制日期")
    private java.util.Date compileDate;
	/**审核状态（1待提交、2待审核、3审核中、4已通过）*/
	@Excel(name = "审核状态（1待提交、2待审核、3审核中、4已通过）", width = 15)
    @ApiModelProperty(value = "审核状态（1待提交、2待审核、3审核中、4已通过）")
    @TableField(value = "`status`")
    @Dict(dicCode = EmergencyDictConstant.YEARPLAN_STATUS)
    private java.lang.Integer status;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
