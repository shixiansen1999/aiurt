package com.aiurt.boot.plan.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/14
 * @time: 14:50
 */

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.plan.entity.EmergencyPlanAtt;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-14 14:50
 */
@Data
public class EmergencyPlanExcelDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**应急预案类型*/
    @Excel(name = "应急预案类型", width = 15,needMerge = true,replace = {"综合应急预案_1","专项应急预案_2","现场处置预案_3"})
    @ApiModelProperty(value = "应急预案类型")
    @Dict(dicCode = "emergency_plan_type")
    private Integer emergencyPlanType;
    /**应急预案名称*/
    @Excel(name = "应急预案名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案名称")
    private String emergencyPlanName;
    /**应急预案关键词*/
    @Excel(name = "应急预案关键词", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案关键词")
    private String keyWord;
    /**应急预案版本*/
    @Excel(name = "应急预案版本", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案版本")
    private String emergencyPlanVersion;
    /**应急预案状态（1未启用、2启用中）*/
    @Excel(name = "启用状态", width = 15,needMerge = true,replace = {"已停用_1","有效_2","空_null"})
    @ApiModelProperty(value = "启用状态（1已停用、2有效）")
    @Dict(dicCode = "emergency_status")
    private Integer status;

    /**编制部门*/
    @ApiModelProperty(value = "编制部门")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText ="depart_name")
    private String orgCode;

    @Excel(name = "编制部门", width = 15,needMerge = true)
    @ApiModelProperty(value = "编制部门")
    private String orgName;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Excel(name = "编制人", width = 15,needMerge = true)
    @ApiModelProperty(value = "编制人")
    private String userName;

    /**状态（1待提交、2待审核、3审核中、4已驳回、5已通过）*/
    @Excel(name = "流程状态", width = 15,needMerge = true,replace = {"待提审_1","待审核_2","审核中_3","已驳回_4","已通过_5"})
    @ApiModelProperty(value = "流程状态（1待提审、2待审核、3审核中、4已驳回、5已通过）")
    @Dict(dicCode = "emergency_plan_status")
    private Integer emergencyPlanStatus;

    @ExcelCollection(name = "处置程序")
    @ApiModelProperty(value = "处置程序")
    @TableField(exist = false)
    List<EmergencyPlanDisposalProcedureExcelDTO> planDisposalProcedureList = new ArrayList<>();

    @ExcelCollection(name = "应急物资")
    @ApiModelProperty(value = "应急物资")
    List<EmergencyPlanMaterialsExcelDTO> planMaterialsDTOList = new ArrayList<>();



}
