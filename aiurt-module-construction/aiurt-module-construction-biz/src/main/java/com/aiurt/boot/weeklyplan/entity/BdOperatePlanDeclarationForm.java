package com.aiurt.boot.weeklyplan.entity;

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

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 周计划表
 * @Author: jeecg-boot
 * @Date:   2021-05-10
 * @Version: V1.0
 */
@Data
@TableName("bd_operate_plan_declaration_form")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_operate_plan_declaration_form对象", description="周计划表")
public class BdOperatePlanDeclarationForm implements Serializable {
    private static final long serialVersionUID = 1L;

	/**计划令表*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "计划令表")
    private String id;
    @Excel(name = "作业性质", width = 10)
    @ApiModelProperty(value = "作业性质")
    private String nature;
	/**作业类别*/
	@Excel(name = "作业类别", width = 15)
    @ApiModelProperty(value = "作业类别")
    private String type;
	/**作业单位id*/
	@Excel(name = "作业单位id", width = 15)
    @ApiModelProperty(value = "作业单位id")
    private String departmentId;
	/**流程时间*/
	@Excel(name = "流程时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "流程时间")
    private Date dateTime;
	/**taskDate*/
	@Excel(name = "taskDate", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "taskDate")
    private Date taskDate;
	/**作业时间*/
	@Excel(name = "作业时间", width = 15)
    @ApiModelProperty(value = "作业时间")
    private String taskTime;
	/**作业范围*/
	@Excel(name = "作业范围", width = 15)
    @ApiModelProperty(value = "作业范围")
    private String taskRange;
	/**供电要求*/
	@Excel(name = "供电要求", width = 15)
    @ApiModelProperty(value = "供电要求")
    private String powerSupplyRequirement;
	/**作业内容*/
	@Excel(name = "作业内容", width = 15)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;
	/**防护措施*/
	@Excel(name = "防护措施", width = 15)
    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;
	/**施工负责人id*/
	@Excel(name = "施工负责人id", width = 15)
    @ApiModelProperty(value = "施工负责人id")
    private String chargeStaffId;
	/**配合部门*/
	@Excel(name = "配合部门", width = 15)
    @ApiModelProperty(value = "配合部门")
    private String coordinationDepartmentId;
	/**请点车站id*/
	@Excel(name = "请点车站id", width = 15)
    @ApiModelProperty(value = "请点车站id")
    private String firstStationId;
	/**销点车站id*/
	@Excel(name = "销点车站id", width = 15)
    @ApiModelProperty(value = "销点车站id")
    private String secondStationId;
    /**变电所*/
    @Excel(name = "变电所", width = 15)
    @ApiModelProperty(value = "变电所")
    private String substationId;
	/**辅站id*/
	@Excel(name = "辅站id", width = 15)
    @ApiModelProperty(value = "辅站id")
    private String assistStationIds;
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
	/**星期1~7对应星期一~星期日*/
	@Excel(name = "星期1~7对应星期一~星期日", width = 15)
    @ApiModelProperty(value = "星期1~7对应星期一~星期日")
    private Integer weekday;
	/**计划令图片*/
	@Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
    private String picture;
	/**计划令录音*/
	@Excel(name = "计划令录音", width = 15)
    @ApiModelProperty(value = "计划令录音")
    private String voice;
	/**0申请中 1 同意 2驳回 3草稿保存（apply_form_status为0可以修改申请条目与状态）*/
	@Excel(name = "0申请中 1 同意 2驳回 3草稿保存（apply_form_status为0可以修改申请条目与状态）", width = 15)
    @ApiModelProperty(value = "0申请中 1 同意 2驳回 3草稿保存（apply_form_status为0可以修改申请条目与状态）")
    private Integer formStatus;
	/**0 申请中 1 流程结束*/
	@Excel(name = "0 申请中 1 流程结束", width = 15)
    @ApiModelProperty(value = "0 申请中 1 流程结束")
    private Integer applyFormStatus;
	/**申请人*/
	@Excel(name = "申请人", width = 15)
    @ApiModelProperty(value = "申请人")
    private String applyStaffId;
	/**线路负责人*/
	@Excel(name = "线路负责人", width = 15)
    @ApiModelProperty(value = "线路负责人")
    private String lineStaffId;
	/**调度人*/
	@Excel(name = "调度人", width = 15)
    @ApiModelProperty(value = "调度人")
    private String dispatchStaffId;
	/**生产调度审批状态 0 未审批 1 通过 2 驳回*/
	@Excel(name = "生产调度审批状态 0 未审批 1 通过 2 驳回", width = 15)
    @ApiModelProperty(value = "生产调度审批状态 0 未审批 1 通过 2 驳回")
    private Integer dispatchFormStatus;
	/**线路负责人审批状态 0 未审批 1 通过 2 驳回*/
	@Excel(name = "线路负责人审批状态 0 未审批 1 通过 2 驳回", width = 15)
    @ApiModelProperty(value = "线路负责人审批状态 0 未审批 1 通过 2 驳回")
    private Integer lineFormStatus;
	/**completionStatus*/
	@Excel(name = "completionStatus", width = 15)
    @ApiModelProperty(value = "completionStatus")
    private String completionStatus;
	/**0正常计划 1计划补修 2日计划补充 3施工变更 4 施工取消*/
	@Excel(name = "0正常计划 1计划补修 2日计划补充 3施工变更 4 施工取消", width = 15)
    @ApiModelProperty(value = "0正常计划 1计划补修 2日计划补充 3施工变更 4 施工取消")
    private Integer planChange;
	/**变更关联的已通过计划令*/
	@Excel(name = "变更关联的已通过计划令", width = 15)
    @ApiModelProperty(value = "变更关联的已通过计划令")
    private String changeCorrelation;
	/**code*/
	@Excel(name = "code", width = 15)
    @ApiModelProperty(value = "code")
    private String code;
	/**directorFormStatus*/
	@Excel(name = "directorFormStatus", width = 15)
    @ApiModelProperty(value = "directorFormStatus")
    private Integer directorFormStatus;
	/**directorStaffId*/
	@Excel(name = "directorStaffId", width = 15)
    @ApiModelProperty(value = "directorStaffId")
    private String directorStaffId;
	/**managerFormStatus*/
	@Excel(name = "managerFormStatus", width = 15)
    @ApiModelProperty(value = "managerFormStatus")
    private Integer managerFormStatus;
	/**managerStaffId*/
	@Excel(name = "managerStaffId", width = 15)
    @ApiModelProperty(value = "managerStaffId")
    private String managerStaffId;
	/**assistStationManagerIds*/
	@Excel(name = "assistStationManagerIds", width = 15)
    @ApiModelProperty(value = "assistStationManagerIds")
    private String assistStationManagerIds;
	/**assistStationManagerNames*/
	@Excel(name = "assistStationManagerNames", width = 15)
    @ApiModelProperty(value = "assistStationManagerNames")
    private String assistStationManagerNames;
	/**rejectedReason*/
	@Excel(name = "rejectedReason", width = 15)
    @ApiModelProperty(value = "rejectedReason")
    private String rejectedReason;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**1是申请计划，2修改，不传取消计划*/
    @TableField(exist = false)
    @ApiModelProperty(value = "1是申请计划，2修改，不传取消计划")
    private Integer isApply;

    /**线路负责人实际审批人*/
    @ApiModelProperty(value = "线路负责人实际审批人")
    private String actualLineStaffId;

    /**工区id*/
    @ApiModelProperty(value = "工区id")
    private String siteId;
    @ApiModelProperty("取消原因")
    private String reason;

}
