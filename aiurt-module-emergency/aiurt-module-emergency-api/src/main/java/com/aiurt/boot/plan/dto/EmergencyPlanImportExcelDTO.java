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
public class EmergencyPlanImportExcelDTO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**应急预案类型*/
    @Excel(name = "应急预案类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案类型")
    private String emergencyPlanType;

    /**应急预案名称*/
    @Excel(name = "应急预案名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案名称")
    private String emergencyPlanName;

    /**应急队伍*/
    @ApiModelProperty(value = "应急队伍")
    private String emergencyTeamId;



    /**应急预案关键词*/
    @Excel(name = "应急预案关键词", width = 15,needMerge = true)
    @ApiModelProperty(value = "应急预案关键词")
    private String keyWord;

    /**应急预案内容*/
    @Excel(name = "应急预案内容", width = 15)
    @ApiModelProperty(value = "应急预案内容")
    private String emergencyPlanContent;


    /**
     * 应急预案错误原因
     */
    @ApiModelProperty(value = "应急预案错误原因")
    @TableField(exist = false)
    private String emergencyPlanErrorReason;

    @ExcelCollection(name = "处置程序")
    @ApiModelProperty(value = "处置程序")
    @TableField(exist = false)
    List<EmergencyPlanDisposalProcedureImportExcelDTO> planDisposalProcedureList = new ArrayList<>();

    @ExcelCollection(name = "应急物资")
    @ApiModelProperty(value = "应急物资")
    List<EmergencyPlanMaterialsImportExcelDTO> planMaterialsDTOList = new ArrayList<>();



}
