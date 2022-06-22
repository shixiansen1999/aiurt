package com.aiurt.boot.strategy.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("inspection_strategy")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="inspection_strategy对象", description="inspection_strategy")
public class InspectionStrategy implements Serializable {
    private static final long serialVersionUID = 1L;

	/**生成年检计划状态,*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "生成年检计划状态,")
    private java.lang.String id;
	/**策略名称*/
	@Excel(name = "策略名称", width = 15)
    @ApiModelProperty(value = "策略名称")
    private java.lang.String name;
	/**年份*/
	@Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "年份")
    private java.lang.Integer year;
	/**周期策略*/
	@Excel(name = "周期策略", width = 15)
    @ApiModelProperty(value = "周期策略")
    private java.lang.Integer tactics;
	/**检修周期类型*/
	@Excel(name = "检修周期类型", width = 15)
    @ApiModelProperty(value = "检修周期类型")
    private java.lang.Integer type;
	/**是否需要验收：0否 1是*/
	@Excel(name = "是否需要验收：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要验收：0否 1是")
    private java.lang.Integer isReceipt;
	/**是否需要确认：0否 1是*/
	@Excel(name = "是否需要确认：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要确认：0否 1是")
    private java.lang.Integer isConfirm;
	/**作业类型（A1不用计划令,A2,A3,B1,B2,B3）*/
	@Excel(name = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）", width = 15)
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private java.lang.Integer workType;
	/**生效状态：0未生效、1已生效*/
	@Excel(name = "生效状态：0未生效、1已生效", width = 15)
    @ApiModelProperty(value = "生效状态：0未生效、1已生效")
    private java.lang.Integer status;
	/**是否委外，0否1是*/
	@Excel(name = "是否委外，0否1是", width = 15)
    @ApiModelProperty(value = "是否委外，0否1是")
    private java.lang.Integer isOutsource;
	/**生成年检计划状态，0未生成 1已生成*/
	@Excel(name = "生成年检计划状态，0未生成 1已生成", width = 15)
    @ApiModelProperty(value = "生成年检计划状态，0未生成 1已生成")
    private java.lang.Integer generateStatus;
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
