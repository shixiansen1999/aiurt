package com.aiurt.modules.weeklyplan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Lai W.
 * @version 1.0
 */

@Data
public class BdOperatePlanDeclarationFormReturnTypeDTO {

    @Excel(name = "Id")
    @ApiModelProperty(value = "计划令表id")
    private Integer id;

    @ApiModelProperty(value = "0申请中 1 同意 2驳回 3草稿保存（apply_form_status为0可以修改申请条目与状态）")
    private Integer formStatus;

    @Excel(name = "作业性质", width = 10)
    @ApiModelProperty(value = "作业性质")
    private String nature;

    @Excel(name = "作业类别", width = 15)
    @ApiModelProperty(value = "作业类别")
    private String type;

    @Excel(name = "作业单位", width = 15)
    @ApiModelProperty(value = "作业单位")
    private String departmentName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "作业日期")
    private Date taskDate;

    @Excel(name = "作业时间", width = 15)
    @ApiModelProperty(value = "作业时间")
    private String taskTime;

    @ApiModelProperty(value = "星期1~7对应星期一~星期日")
    private Integer weekday;

    @Excel(name = "线路作业范围", width = 30)
    @ApiModelProperty(value = "作业范围")
    private String taskRange;

    @Excel(name = "供电要求", width = 80)
    @ApiModelProperty(value = "供电要求")
    private String powerSupplyRequirement;

    @Excel(name = "作业内容", width = 50)
    @ApiModelProperty(value = "作业内容")
    private String taskContent;

    @Excel(name = "防护措施", width = 25)
    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasure;

    @Excel(name = "施工负责人", width = 15)
    @ApiModelProperty(value = "施工负责人")
    private String chargeStaffName;

    @Excel(name = "配合部门", width = 30)
    @ApiModelProperty(value = "配合部门")
    private String coordinationDepartmentId;

    @Excel(name = "请点车站")
    @ApiModelProperty(value = "请点车站")
    private String firstStationName;

    @ApiModelProperty(value = "请点车站id")
    private String firstStationId;

    @Excel(name = "销点车站")
    @ApiModelProperty(value = "销点车站")
    private String secondStationName;

    @ApiModelProperty(value = "销点车站id")
    private String secondStationId;

    @Excel(name = "辅站", width = 30)
    @ApiModelProperty(value = "辅站")
    private String assistStationName;

    @ApiModelProperty(value = "辅站id")
    private String assistStationIds;

    @ApiModelProperty(value = "辅站负责人")
    private String assistStationManagerNames;

    @ApiModelProperty(value = "辅站负责人id")
    private String assistStationManagerIds;

    @Excel(name = "作业人数")
    @ApiModelProperty(value = "作业人数")
    private Integer taskStaffNum;

    @Excel(name = "大中型器具")
    @ApiModelProperty(value = "大中型器具")
    private String largeAppliances;

    @ApiModelProperty(value = "线路负责人")
    private String lineStaffName;

    @ApiModelProperty(value = "调度人")
    private String dispatchStaffName;

    @ApiModelProperty(value = "主任")
    private String directorStaffName;

    @ApiModelProperty(value = "经理")
    private String managerStaffName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "线路")
    private Integer lineID;

    @ApiModelProperty(value = "planChange")
    private Integer planChange;

    @ApiModelProperty(value = "changeCorrelation")
    private Integer changeCorrelation;

    @ApiModelProperty(value = "生产调度审批状态 0 未审批 1 通过 2 驳回")
    private Integer dispatchFormStatus;

    @ApiModelProperty(value = "线路负责人审批状态 0 未审批 1 通过 2 驳回")
    private Integer lineFormStatus;

    @ApiModelProperty(value = "经理审批状态 0 未审批 1 通过 2 驳回")
    private Integer managerFormStatus;

    @ApiModelProperty(value = "主任审批状态 0 未审批 1 通过 2 驳回")
    private Integer directorFormStatus;

    @ApiModelProperty(value = "线路负责人id")
    private String lineStaffId;

    @ApiModelProperty(value = "生产调度id")
    private String dispatchStaffId;

    @ApiModelProperty(value = "主任id")
    private String directorStaffId;

    @ApiModelProperty(value = "经理id")
    private String managerStaffId;

    @ApiModelProperty(value = "施工负责人id")
    private String chargeStaffId;

    @ApiModelProperty(value = "申请人id")
    private String applyStaffId;

    @ApiModelProperty(value = "计划令图片")
    private String picture;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "计划令录音")
    private String voice;

    @ApiModelProperty(value = "驳回原因")
    private String rejectedReason;

    private Integer applyFormStatus;
    @ApiModelProperty(value = "count")
    private Integer count;


    @ApiModelProperty(value = "申请人名称")
    private String applyStaffName;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "是否可以结束")
    private Integer isCanEnd;

    @ApiModelProperty(value = "手机号")
    private String phone;
    @ApiModelProperty(value = "证件编号")
    private String identityNumber;

    @ApiModelProperty(value = "工区id")
    private Integer siteId;
    @ApiModelProperty(value = "工区名称")
    private String siteName;

    @ApiModelProperty("作业时间")
    @TableField(exist = false)
    private String timeFormat;
    @ApiModelProperty("取消原因")
    private String reason;
    @ApiModelProperty("变电所id")
    private String substationId;
    @ApiModelProperty(value = "变电所名称")
    @TableField(exist = false)
    private String substationName;
}
