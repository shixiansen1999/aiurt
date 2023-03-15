package com.aiurt.boot.plan.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_plan")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_plan对象", description="patrol_plan")
public class PatrolPlan extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**计划编号*/
	@Excel(name = "计划编号", width = 15)
    @ApiModelProperty(value = "计划编号")
    private java.lang.String code;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
    private java.lang.String name;
	/**作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3*/
	@Excel(name = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3", width = 15)
    @ApiModelProperty(value = "作业类型：1 A1、2 A2、3 A3、4 B1、5 B2、6 C1、7 C2、8 C3")
    private java.lang.Integer type;
	/**是否委外：0否、1是*/
	@Excel(name = "是否委外：0否、1是", width = 15)
    @ApiModelProperty(value = "是否委外：0否、1是")
    @Dict(dicCode = "patrol_outsource")
    private java.lang.Integer outsource;
	/**巡检频次：1 一天1次、2 一周1次、3 一周2次、 4 一月1次、 5一月2次、6两天1次、7三天1次*/
	@Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次、 4 一月1次、 5一月2次、6两天1次、7三天1次", width = 15)
    @ApiModelProperty(value = "1 一天1次、2 一周1次、3 一周2次、 4 一月1次、 5一月2次、6两天1次、7三天1次")
    @Dict(dicCode = "patrol_plan_period")
    private java.lang.Integer period;
	/**生效状态：0停用、1启用*/
	@Excel(name = "生效状态：0停用、1启用", width = 15)
    @ApiModelProperty(value = "生效状态：0停用、1启用")
    @Dict(dicCode = "patrol_plan_status")
    private java.lang.Integer status;
	/**有效开始日期*/
	@Excel(name = "有效开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效开始日期")
    private java.util.Date startDate;
	/**有效结束时间*/
	@Excel(name = "有效结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "有效结束时间")
    private java.util.Date endDate;
	/**是否需要审核：0否 1是*/
	@Excel(name = "是否需要审核：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否 1是")
    private java.lang.Integer confirm;
    /**是否已生成任务：0否 1是*/
    @Excel(name = "是否已生成任务：0否 1是", width = 15)
    @ApiModelProperty(value = "是否已生成任务：0否 1是")
    private java.lang.Integer created;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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
