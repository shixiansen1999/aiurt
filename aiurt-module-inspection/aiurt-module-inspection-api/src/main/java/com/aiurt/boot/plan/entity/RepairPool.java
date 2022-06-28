package com.aiurt.boot.plan.entity;

import com.aiurt.common.aspect.annotation.Dict;
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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_pool")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "repair_pool对象", description = "repair_pool")
public class RepairPool implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
    /**
     * 检修计划名称
     */
    @Excel(name = "检修计划名称", width = 15)
    @ApiModelProperty(value = "检修计划名称")
    private java.lang.String name;
    /**
     * 检修计划单号
     */
    @Excel(name = "检修计划单号", width = 15)
    @ApiModelProperty(value = "检修计划单号")
    private java.lang.String code;
    /**
     * 周数，对于年来计算的周数
     */
    @Excel(name = "周数，对于年来计算的周数", width = 15)
    @ApiModelProperty(value = "周数，对于年来计算的周数")
    private java.lang.Integer weeks;
    /**
     * 检修周期类型：0周检、1月检、2双月检、3季检、4半年检、5年检
     */
    @Excel(name = "检修周期类型：0周检、1月检、2双月检、3季检、4半年检、5年检", width = 15)
    @ApiModelProperty(value = "检修周期类型：0周检、1月检、2双月检、3季检、4半年检、5年检")
    @Dict(dicCode = "inspection_cycle_type")
    @NotNull(message = "请选择检修周期类型")
    private java.lang.Integer type;
    /**
     * 检修策略编码，关联inspection_strategy的code
     */
    @Excel(name = "检修策略编码，关联inspection_strategy的code", width = 15)
    @ApiModelProperty(value = "检修策略编码，关联inspection_strategy的code")
    private java.lang.String inspectionStrCode;
    /**检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成*/
    @Excel(name = "检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成 ", width = 15)
    @ApiModelProperty(value = "检修状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7待验收、8已完成 ")
    @Dict(dicCode ="inspection_task_state")
    private java.lang.Integer status;
    /**
     * 开始时间
     */
    @Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
    /**
     * 结束时间
     */
    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
    /**
     * 是否需要审核：0否 1是
     */
    @Excel(name = "是否需要审核：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要审核：0否 1是")
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要审核")
    private java.lang.Integer isConfirm;
    /**
     * 是否需要验收：0否 1是
     */
    @Excel(name = "是否需要验收：0否 1是", width = 15)
    @ApiModelProperty(value = "是否需要验收：0否 1是")
    @Dict(dicCode = "inspection_is_confirm")
    @NotNull(message = "请选择是否需要验收")
    private java.lang.Integer isReceipt;
    /**是否委外：0否1是*/
    @Excel(name = "是否委外：0否1是", width = 15)
    @ApiModelProperty(value = "是否委外：0否1是")
    @Dict(dicCode = "inspection_is_manual")
    @NotNull(message = "请选择是否委外")
    private java.lang.Integer isOutsource;

    /**
     * 作业类型（A1不用计划令,A2,A3,B1,B2,B3）
     */
    @Excel(name = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）", width = 15)
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    @Dict(dicCode = "work_type")
    @NotNull(message = "请选择作业类型")
    private java.lang.Integer workType;
    /**
     * 是否是手工下发任务，0否1是
     */
    @Excel(name = "是否是手工下发任务，0否1是", width = 15)
    @ApiModelProperty(value = "是否是手工下发任务，0否1是")
    private java.lang.Integer isManual;
    /**
     * 退回理由，任务被退回时必须填写
     */
    @Excel(name = "退回理由，任务被退回时必须填写", width = 15)
    @ApiModelProperty(value = "退回理由，任务被退回时必须填写")
    private java.lang.String remark;
    /**
     * 删除状态，0.未删除 1.已删除
     */
    @Excel(name = "删除状态，0.未删除 1.已删除", width = 15)
    @ApiModelProperty(value = "删除状态，0.未删除 1.已删除")
    private java.lang.Integer delFlag;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;

    /**
     * 站点名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    /**
     * 组织结构名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "组织结构名称")
    private String orgName;

    /**
     * 检修周期类型名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "检修周期类型名称")
    private String typeName;

    /**
     * 适用专业名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "适用专业名称")
    private String majorName;

    /**
     * 适用专业子系统名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "适用专业子系统名称")
    private String subsystemName;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "状态名称")
    private String statusName;
}
