package com.aiurt.boot.plan.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/14
 * @time: 14:50
 */

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-14 14:50
 */
@Data
public class EmergencyPlanRecordImportExcelDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**应急预案类型*/
    @Excel(name = "事件类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "事件类型")
    private String eventClass;

    @Excel(name = "事件性质", width = 15)
    @ApiModelProperty(value = "事件性质")
    private String eventProperty;

    /**应急预案名称*/
    @Excel(name = "应急预案名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案名称")
    private String emergencyPlanName;

    @Excel(name = "应急预案版本", width = 15)
    @ApiModelProperty(value = "应急预案版本")
    private String emergencyPlanVersion;

    @Excel(name = "启动日期", width = 15)
    @ApiModelProperty(value = "启动日期")
    private String starttime;

    @Excel(name = "参与部门", width = 15)
    @ApiModelProperty(value = "参与部门")
    private String emergencyPlanRecordDepartId;

    /**应急队伍*/
    @Excel(name = "应急队伍名称", width = 15)
    @ApiModelProperty(value = "应急队伍名称")
    private String emergencyTeamId;

    @Excel(name = "对完成预案及其他应急管理工作建议", width = 15)
    @ApiModelProperty(value = "对完成预案及其他应急管理工作建议")
    private String advice;


    /**
     * 应急预案错误原因
     */
    @ApiModelProperty(value = "应急预案启动记录错误原因")
    @TableField(exist = false)
    private String emergencyPlanRecordErrorReason;

    @ExcelCollection(name = "处置程序")
    @ApiModelProperty(value = "处置程序")
    @TableField(exist = false)
    List<EmergencyPlanRecordDisposalProcedureImportExcelDTO> planRecordDisposalProcedureList ;

    @ExcelCollection(name = "应急物资")
    @ApiModelProperty(value = "应急物资")
    List<EmergencyPlanRecordMaterialsImportExcelDTO> planRecordMaterialsList;

    @ExcelCollection(name = "问题及措施")
    @ApiModelProperty(value = "问题及措施")
    List<EmergencyPlanRecordProblemMeasuresImportExcelDTO> ProblemMeasuresList;



}
