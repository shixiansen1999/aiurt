package com.aiurt.boot.plan.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
public class EmergencyPlanRecordExcelDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**事件分类*/
    @Excel(name = "事件类型", width = 15)
    @ApiModelProperty(value = "事件类型")
    @Dict(dicCode = "emergency_event_class")
    private Integer eventClass;
    /**事件性质*/
    @Excel(name = "事件性质", width = 15)
    @ApiModelProperty(value = "事件性质")
    @Dict(dicCode = "emergency_event_property")
    private Integer eventProperty;
    /**应急预案id*/
    @Excel(name = "应急预案id", width = 15)
    @ApiModelProperty(value = "应急预案id")
    private String emergencyPlanId;
    /**应急预案版本*/
    @Excel(name = "应急预案版本", width = 15)
    @ApiModelProperty(value = "应急预案版本")
    private String emergencyPlanVersion;

    /**启动应急预案版本*/
    @Excel(name = "启动应急预案", width = 15)
    @ApiModelProperty(value = "启动应急预案")
    @TableField(exist = false)
    private String planVersion;

    /**记录人部门*/
    @Excel(name = "记录人部门", width = 15)
    @ApiModelProperty(value = "记录人部门")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText ="depart_name")
    private String orgCode;

    /**启动日期*/
    @Excel(name = "启动日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "启动日期")
    private java.util.Date starttime;
    /**对完成预案及其他应急管理工作建议*/
    @Excel(name = "对完成预案及其他应急管理工作建议", width = 15)
    @ApiModelProperty(value = "对完成预案及其他应急管理工作建议")
    private String advice;
    /**记录人*/
    @Excel(name = "记录人", width = 15)
    @ApiModelProperty(value = "记录人")
    @Dict(dictTable = "sys_user",dicCode = "username",dicText = "realname")
    private String recorderId;
    /**记录时间*/
    @Excel(name = "记录时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "记录时间")
    private java.util.Date recordTime;
    /**删除状态： 0未删除 1已删除*/
    @Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;

    /**提交状态： 0待提交 1已提交*/
    @Excel(name = "提交状态： 0未提交 1已提交", width = 15)
    @ApiModelProperty(value = "提交状态： 0待提交 1已提交")
    @Dict(dicCode = "emergency_is_commit")
    private Integer status;

    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**应急预案启动记录应急队伍*/
    @ApiModelProperty(value = "应急预案启动记录应急救援队伍")
    private List<String> emergencyPlanRecordTeamId;

    /**应急预案启动记录参与部门*/
    @ApiModelProperty(value = "应急预案启动记录参与部门")
    private List<String> emergencyPlanRecordDepartId;
    /**
     * 参与部门信息名称,英文分号分隔
     */
    @ApiModelProperty(value = "参与部门信息名称,英文分号分隔")
    private String deptNames;
    /**
     * 应急预案启动记录处置程序
     */
    @ApiModelProperty(value = "应急预案启动记录处置程序")
    private List<EmergencyPlanRecordDisposalProcedure> emergencyPlanRecordDisposalProcedureList;

    /**
     * 应急预案启动记录应急物资
     */
    @ApiModelProperty(value = "应急预案启动记录应急物资")
    private List<EmergencyPlanRecordMaterials> emergencyPlanRecordMaterialsList;



}
