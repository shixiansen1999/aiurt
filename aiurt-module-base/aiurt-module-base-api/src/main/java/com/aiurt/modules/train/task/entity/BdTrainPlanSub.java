package com.aiurt.modules.train.task.entity;

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
import com.aiurt.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 年子计划
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
@Data
@TableName("bd_train_plan_sub")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_train_plan_sub对象", description="年子计划")
public class BdTrainPlanSub implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**子计划编码*/
	@Excel(name = "子计划编码", width = 15)
    @ApiModelProperty(value = "子计划编码")
    private String planCode;
	/**培训类别*/
	@Excel(name = "培训类别", width = 15)
    @ApiModelProperty(value = "培训类别")
    @Dict(dicCode = "classify_state")
    private Integer classify;

    @TableField(exist = false)
    @ApiModelProperty(value = "培训类别名称")
    private String classifyName;

	/**培训课程*/
	@Excel(name = "培训课程", width = 15)
    @ApiModelProperty(value = "培训课程")
    private String courseName;
	/**培训时间*/
	@Excel(name = "培训时间", width = 15)
    @ApiModelProperty(value = "培训时间")
    private String planTime;
	/**培训课时*/
	@Excel(name = "培训课时", width = 15)
    @ApiModelProperty(value = "培训课时")
    private Integer courseHours;
	/**外键*/
	@Excel(name = "外键", width = 15)
    @ApiModelProperty(value = "外键")
    private String planId;

    /**状态（0：未使用；1：已使用）*/
    @Excel(name = "状态（0：未使用；1：已使用）", width = 15)
    @ApiModelProperty(value = "状态（0：未使用；1：已使用）")
    @TableField(exist = false)
    @Dict(dicCode = "planSub_state")
    private Integer state;

    /**年份*/
    @Excel(name = "年份", width = 15)
    @ApiModelProperty(value = "年份")
    @TableField(exist = false)
    private Integer planYear;


    /**部门id*/
    @Excel(name = "部门id", width = 15)
    @ApiModelProperty(value = "部门id")
    @TableField(exist = false)
    private Integer deptId;
}
