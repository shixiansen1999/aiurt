package com.aiurt.boot.entity.patrol.plan;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class PatrolPlan implements Serializable {
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
    private java.lang.Integer outsource;
	/**巡检频次：1 一天1次、2 一周1次、3 一周2次*/
	@Excel(name = "巡检频次：1 一天1次、2 一周1次、3 一周2次", width = 15)
    @ApiModelProperty(value = "巡检频次：1 一天1次、2 一周1次、3 一周2次")
    private java.lang.Integer period;
	/**生效状态：0停用、1启用*/
	@Excel(name = "生效状态：0停用、1启用", width = 15)
    @ApiModelProperty(value = "生效状态：0停用、1启用")
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
