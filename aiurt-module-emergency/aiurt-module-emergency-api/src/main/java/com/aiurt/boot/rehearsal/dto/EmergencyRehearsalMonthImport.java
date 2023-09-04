package com.aiurt.boot.rehearsal.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class EmergencyRehearsalMonthImport {

    /**演练类型(1专项应急预案、2综合应急预案、3现场处置方案)*/
    @Excel(name = "演练类型")
    @ApiModelProperty(value = "演练类型(1专项应急预案、2综合应急预案、3现场处置方案)")
    private java.lang.String typeName;

    private java.lang.Integer type;

    /**演练科目*/
    @Excel(name = "演练科目")
    @ApiModelProperty(value = "演练科目")
    private java.lang.String subject;
    /**依托预案名称*/
    @Excel(name = "依托预案")
    @ApiModelProperty(value = "依托预案名称")
    private java.lang.String schemeName;
    /**预案版本*/
    @Excel(name = "预案版本")
    @ApiModelProperty(value = "预案版本")
    private java.lang.String schemeVersion;

    private java.lang.String schemeId;

    /**演练形式(1实战演练、2桌面推演)*/
    @Excel(name = "演练形式")
    @ApiModelProperty(value = "演练形式(1实战演练、2桌面推演)")
    private java.lang.String modalityName;

    private java.lang.Integer modality;

    /**组织部门*/
    @Excel(name = "组织部门")
    @ApiModelProperty(value = "组织部门")
    private java.lang.String orgName;

    private java.lang.String orgCode;

    /**演练时间，格式yyyy-MM*/
    @Excel(name = "演练时间", width = 15, orderNum = "4", format = "MM月")
    @ApiModelProperty(value = "演练时间，格式yyyy-MM")
    private java.lang.String rehearsalTime;
    /**必须体现环节*/
    @Excel(name = "必须体现环节", orderNum = "6", width = 15)
    @ApiModelProperty(value = "必须体现环节")
    private java.lang.String step;
    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    private String mistake;
}
