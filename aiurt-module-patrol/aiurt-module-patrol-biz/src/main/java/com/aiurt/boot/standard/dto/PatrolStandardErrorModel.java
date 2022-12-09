package com.aiurt.boot.standard.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/25
 * @desc
 */
@Data
public class PatrolStandardErrorModel {
    /**巡检表名*/
    @Excel(name = "巡视标准表名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "巡视标准表名称")
    private java.lang.String name;
    /**专业code*/
    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code")
    private java.lang.String professionCode;
    /**专业code*/
    @Excel(name = "适用子系统", width = 15,needMerge = true)
    @ApiModelProperty(value = "适用系统code")
    private java.lang.String subsystemCode;
    /**与设备类型相关：0否 1 是*/
    @Excel(name = "是否与设备类型相关", width = 15,needMerge = true)
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    @TableField(exist = false)
    private java.lang.String isDeviceType;
    /**指定具体设备：0否 1 是*/
    @ApiModelProperty(value = "指定具体设备：0否 1 是")
    private java.lang.Integer specifyDevice;
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "生效状态：0停用 1启用")
    @TableField(exist = false)
    private java.lang.String statusName;
    /**设备类型code*/
    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String deviceTypeName;
    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  standMistake;


    /**层级类型：0一级、1子级*/
    @Excel(name = "层级类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "层级类型：0一级、1子级")
    @TableField(exist = false)
    private java.lang.String hierarchyTypeName;
    /**父级*/
    @Excel(name = "父级", width = 15,needMerge = true)
    @ApiModelProperty(value = "父级")
    @TableField(exist = false)
    private java.lang.String parent;
    /**巡检项内容*/
    @Excel(name = "巡检项内容", width = 15,needMerge = true)
    @ApiModelProperty(value = "巡检项内容")
    private java.lang.String content;
    /**巡检项编号*/
    @Excel(name = "巡检项编号", width = 15,needMerge = true)
    @ApiModelProperty(value = "巡检项编号")
    @TableField(value = "`code`")
    private java.lang.String code;
    /**内容排序*/
    @ApiModelProperty(value = "内容排序")
    @TableField(value = "`order`")
    private java.lang.String order;
    /**内容排序*/
    @Excel(name = "内容排序", width = 15,needMerge = true)
    @ApiModelProperty(value = "内容排序")
    @TableField(value = "`order`")
    private java.lang.String detailOrder;
    /**是否为巡检项目：0否 1是*/
    @Excel(name = "是否为巡检项目", width = 15,needMerge = true)
    @ApiModelProperty(value = "是否为巡检项目：0否 1是")
    @TableField(exist = false)
    private java.lang.String checkName;
    /**质量标准*/
    @Excel(name = "质量标准", width = 15,needMerge = true)
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "质量标准")
    private java.lang.String qualityStandard;
    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  itemParentMistake;
}
