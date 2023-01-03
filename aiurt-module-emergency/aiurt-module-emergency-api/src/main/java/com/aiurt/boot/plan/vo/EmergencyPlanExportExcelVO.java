package com.aiurt.boot.plan.vo;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/14
 * @time: 14:50
 */


import com.aiurt.boot.plan.dto.EmergencyPlanDisposalProcedureImportExcelDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsExcelDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanMaterialsImportExcelDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanDisposalProcedure;
import com.aiurt.boot.plan.entity.EmergencyPlanMaterials;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-14 14:50
 */
@Data
public class EmergencyPlanExportExcelVO {
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**应急预案类型*/
    @Excel(name = "应急预案类型", width = 15)
    @ApiModelProperty(value = "应急预案类型")
    private String emergencyPlanType;

    /**应急预案名称*/
    @Excel(name = "应急预案名称", width = 15)
    @ApiModelProperty(value = "应急预案名称")
    private String emergencyPlanName;

    /**应急队伍*/
    @ApiModelProperty(value = "应急队伍")
    private String emergencyTeamId;

    /**应急预案关键词*/
    @Excel(name = "应急预案关键词", width = 15)
    @ApiModelProperty(value = "应急预案关键词")
    private String keyWord;

    /**应急预案内容*/
    @Excel(name = "应急预案内容", width = 15)
    @ApiModelProperty(value = "应急预案内容")
    private String emergencyPlanContent;


    @ApiModelProperty(value = "处置程序")
    @TableField(exist = false)
    List<EmergencyPlanDisposalProcedure> planDisposalProcedureList ;

    @ApiModelProperty(value = "应急物资")
    List<EmergencyPlanMaterialsExportExcelVO> planMaterialsList;



}
