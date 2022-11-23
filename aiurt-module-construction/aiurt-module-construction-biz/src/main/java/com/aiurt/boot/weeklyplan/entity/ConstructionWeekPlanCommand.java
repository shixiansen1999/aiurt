package com.aiurt.boot.weeklyplan.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date:   2022-11-22
 * @Version: V1.0
 */
@Data
@TableName("construction_week_plan_command")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="construction_week_plan_command对象", description="construction_week_plan_command")
public class ConstructionWeekPlanCommand implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**计划令编号*/
	@Excel(name = "计划令编号", width = 15)
    @ApiModelProperty(value = "计划令编号")
    @TableField(value = "`code`")
    private String code;
	/**作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)*/
	@Excel(name = "作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)", width = 15)
    @ApiModelProperty(value = "作业类别(1:A1、2:A2、3:A3、4:B1、5::C1、6:C2)")
    private Integer type;
    /**计划类型(1正常计划 2日补充计划 3临时补修计划*/
    @Excel(name = "计划类型(1正常计划 2日补充计划 3临时补修计划", width = 15)
    @ApiModelProperty(value = "计划类型(1正常计划 2日补充计划 3临时补修计划")
	private Integer planChange;
	/**作业单位ID*/
	@Excel(name = "作业单位ID", width = 15)
    @ApiModelProperty(value = "作业单位ID")
    private String orgCode;
	/**作业日期*/
	@Excel(name = "作业日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "作业日期")
    private Date taskDate;
	/**作业开始时间*/
	@Excel(name = "作业开始时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "作业开始时间")
    private Date taskStartTime;
    /**作业结束时间*/
    @Excel(name = "作业时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "作业结束时间")
    private Date taskEndTime;
	/**作业范围*/
	@Excel(name = "作业范围", width = 15)
    @ApiModelProperty(value = "作业范围")
    private String taskRange;
    /**作业线路编码*/
    @Excel(name = "作业线路编码", width = 15)
    @ApiModelProperty(value = "作业线路编码")
	private String lineCode;
	/**供电要求ID*/
	@Excel(name = "供电要求ID", width = 15)
    @ApiModelProperty(value = "供电要求ID")
    private String powerSupplyRequirementId;
	/**供电要求内容*/
	@Excel(name = "供电要求内容", width = 15)
    @ApiModelProperty(value = "供电要求内容")
    private String powerSupplyRequirementContent;
	/**作业内容*/
	@Excel(name = "作业内容", width = 15)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;
	/**防护措施*/
	@Excel(name = "防护措施", width = 15)
    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;
	/**施工负责人ID*/
	@Excel(name = "施工负责人ID", width = 15)
    @ApiModelProperty(value = "施工负责人ID")
    private String chargeStaffId;
	/**配合部门编码*/
	@Excel(name = "配合部门编码", width = 15)
    @ApiModelProperty(value = "配合部门编码")
    private String coordinationDepartmentCode;
	/**请点车站编码*/
	@Excel(name = "请点车站编码", width = 15)
    @ApiModelProperty(value = "请点车站编码")
    private String firstStationCode;
	/**变电所编码*/
	@Excel(name = "变电所编码", width = 15)
    @ApiModelProperty(value = "变电所编码")
    private String substationCode;
	/**销点车站编码*/
	@Excel(name = "销点车站编码", width = 15)
    @ApiModelProperty(value = "销点车站编码")
    private String secondStationCode;
	/**作业人数*/
	@Excel(name = "作业人数", width = 15)
    @ApiModelProperty(value = "作业人数")
    private Integer taskStaffNum;
	/**大中型器具*/
	@Excel(name = "大中型器具", width = 15)
    @ApiModelProperty(value = "大中型器具")
    private String largeAppliances;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)*/
	@Excel(name = "星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)", width = 15)
    @ApiModelProperty(value = "星期(1:星期一、2:星期二、3:星期三、4:星期四、5:星期五、6:星期六、7:星期日)")
    private Integer weekday;
	/**计划令图片*/
	@Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private String picture;
	/**计划令录音*/
	@Excel(name = "计划令录音", width = 15)
    @ApiModelProperty(value = "计划令录音")
    private String voice;
	/**计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)*/
	@Excel(name = "计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)", width = 15)
    @ApiModelProperty(value = "计划令状态(0待提审、1待审核、2审核中、3已驳回、4已取消、5已通过)")
    private Integer formStatus;
	/**申请人ID*/
	@Excel(name = "申请人ID", width = 15)
    @ApiModelProperty(value = "申请人ID")
    private String applyId;
	/**线路负责人ID*/
	@Excel(name = "线路负责人ID", width = 15)
    @ApiModelProperty(value = "线路负责人ID")
    private String lineUserId;
	/**调度人ID*/
	@Excel(name = "调度人ID", width = 15)
    @ApiModelProperty(value = "调度人ID")
    private String dispatchId;
	/**分部主任ID*/
	@Excel(name = "分部主任ID", width = 15)
    @ApiModelProperty(value = "分部主任ID")
    private String directorId;
	/**中心经理ID*/
	@Excel(name = "中心经理ID", width = 15)
    @ApiModelProperty(value = "中心经理ID")
    private String managerId;
	/**生产调度审批状态(0:未审批、1:通过、2:驳回)*/
	@Excel(name = "生产调度审批状态(0:未审批、1:通过、2:驳回)", width = 15)
    @ApiModelProperty(value = "生产调度审批状态(0:未审批、1:通过、2:驳回)")
    private Integer dispatchStatus;
	/**线路负责人审批状态(0:未审批、1:通过、2:驳回)*/
	@Excel(name = "线路负责人审批状态(0:未审批、1:通过、2:驳回)", width = 15)
    @ApiModelProperty(value = "线路负责人审批状态(0:未审批、1:通过、2:驳回)")
    private Integer lineStatus;
	/**分部主任审批状态(0:未审批、1:通过、2:驳回)*/
	@Excel(name = "分部主任审批状态(0:未审批、1:通过、2:驳回)", width = 15)
    @ApiModelProperty(value = "分部主任审批状态(0:未审批、1:通过、2:驳回)")
    private Integer directorStatus;
	/**中心经理审批状态(0:未审批、1:通过、2:驳回)*/
	@Excel(name = "中心经理审批状态(0:未审批、1:通过、2:驳回)", width = 15)
    @ApiModelProperty(value = "中心经理审批状态(0:未审批、1:通过、2:驳回)")
    private Integer managerStatus;
	/**生产调度驳回原因*/
	@Excel(name = "生产调度驳回原因", width = 15)
    @ApiModelProperty(value = "生产调度驳回原因")
    private String dispatchReason;
	/**线路负责人驳回原因*/
	@Excel(name = "线路负责人驳回原因", width = 15)
    @ApiModelProperty(value = "线路负责人驳回原因")
    private String lineReason;
	/**分部主任驳回原因*/
	@Excel(name = "分部主任驳回原因", width = 15)
    @ApiModelProperty(value = "分部主任驳回原因")
    private String directorReason;
	/**中心经理驳回原因*/
	@Excel(name = "中心经理驳回原因", width = 15)
    @ApiModelProperty(value = "中心经理驳回原因")
    private String managerReason;
    /**生产调度审核意见*/
    @Excel(name = "生产调度审核意见", width = 15)
    @ApiModelProperty(value = "生产调度审核意见")
    private String dispatchOpinion;
    /**线路负责人审核意见*/
    @Excel(name = "线路负责人审核意见", width = 15)
    @ApiModelProperty(value = "线路负责人审核意见")
    private String lineOpinion;
    /**分部主任审核意见*/
    @Excel(name = "分部主任审核意见", width = 15)
    @ApiModelProperty(value = "分部主任审核意见")
    private String directorOpinion;
    /**中心经理驳回原因*/
    @Excel(name = "中心经理审核意见", width = 15)
    @ApiModelProperty(value = "中心经理审核意见")
    private String managerOpinion;
	/**取消用户ID*/
	@Excel(name = "取消用户ID", width = 15)
    @ApiModelProperty(value = "取消用户ID")
    private String cancelId;
	/**取消原因*/
	@Excel(name = "取消原因", width = 15)
    @ApiModelProperty(value = "取消原因")
    private String cancelReason;
	/**工区编码*/
	@Excel(name = "工区编码", width = 15)
    @ApiModelProperty(value = "工区编码")
    private String siteCode;
	/**作业性质(1:施工作业、2:巡检作业)*/
	@Excel(name = "作业性质(1:施工作业、2:巡检作业)", width = 15)
    @ApiModelProperty(value = "作业性质(1:施工作业、2:巡检作业)")
    private Integer nature;
    /**辅站信息*/
    @Excel(name = "辅站信息", width = 15)
    @ApiModelProperty(value = "辅站信息")
    @TableField(exist = false)
    private List<ConstructionCommandAssist> constructionAssist;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
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
}
