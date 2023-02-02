package com.aiurt.modules.system.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
/**
 * @author zwl
 * @version 1.0
 * @date 2023/2/1
 * @desc
 */
public class SysDepartModel {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;


    /**机构名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "机构名称")
    @Excel(name = "机构名称", width = 15)
    private java.lang.String departName;

    /**机构全称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "机构全称")
    @Excel(name = "机构全称", width = 15)
    private java.lang.String departFullName;

    /**机构类型*/
    @TableField(exist = false)
    @ApiModelProperty(value = "机构类型")
    @Excel(name = "机构类型", width = 15)
    private java.lang.String orgCategory;


    /**班组类型*/
    @TableField(exist = false)
    @ApiModelProperty(value = "班组类型")
    @Excel(name = "班组类型", width = 15)
    private java.lang.String teamType;


    /**机构电话*/
    @TableField(exist = false)
    @ApiModelProperty(value = "机构电话")
    @Excel(name = "机构电话", width = 15)
    private java.lang.String departPhoneNum;


    /**联系人*/
    @TableField(exist = false)
    @ApiModelProperty(value = "联系人")
    @Excel(name = "联系人", width = 15)
    private java.lang.String contactId;


    /**联系方式*/
    @TableField(exist = false)
    @ApiModelProperty(value = "联系方式")
    @Excel(name = "联系方式", width = 15)
    private java.lang.String concatWay;


    /**管理负责人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "管理负责人")
    @Excel(name = "管理负责人", width = 15)
    private java.lang.String managerName;


    /**管理负责人id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "管理负责人id")
    private java.lang.String managerId;


    /**技术负责人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "技术负责人")
    @Excel(name = "技术负责人", width = 15)
    private java.lang.String technicalName;


    /**技术负责人id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "技术负责人id")
    private java.lang.String technicalId;


    /**职能*/
    @TableField(exist = false)
    @ApiModelProperty(value = "职能")
    @Excel(name = "职能", width = 15)
    private java.lang.String memo;


    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  sysDepartMistake;


}
