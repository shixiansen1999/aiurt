package com.aiurt.modules.outsourcingpersonnel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : sbx
 * @Classname : OutsourcingPersonnelInput
 * @Description : 委外人员
 * @Date : 2023/7/24 9:31
 */
@Data
@ExcelTarget("OutsourcingPersonnelInput")
public class OutsourcingPersonnelInput {
    /**人员名称*/
    @Excel(name = "人员名称", width = 15)
    @ApiModelProperty(value = "人员名称")
    private  String  name;

    /**所属单位*/
    @Excel(name = "所属单位", width = 15)
    @ApiModelProperty(value = "所属单位")
    private  String  companyName;

    @ApiModelProperty(value = "所属单位")
    private  String  company;

    /**职位名称*/
    @Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
    private  String  position;

    /**所属专业系统*/
    @Excel(name = "所属专业系统", width = 15)
    @ApiModelProperty(value = "所属专业系统")
    private String systemName;

    @ApiModelProperty(value = "所属专业系统编号")
    private String systemCode;

    /**联系方式*/
    @Excel(name = "联系方式", width = 15)
    @ApiModelProperty(value = "联系方式")
    private  String  connectionWay;

    /**施工证编号*/
    @Excel(name = "施工证编号", width = 15)
    @ApiModelProperty(value = "施工证编号")
    private  String  certificateCode;

    /**导入错误原因*/
    @Excel(name = "导入错误原因", width = 15)
    @ApiModelProperty(value = "导入错误原因")
    private String mistake;
}