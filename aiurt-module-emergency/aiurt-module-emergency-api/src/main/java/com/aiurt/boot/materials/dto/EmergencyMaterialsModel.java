package com.aiurt.boot.materials.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/12/16
 * @desc
 */
@Data
public class EmergencyMaterialsModel {

    /**物资id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资id")
    private java.lang.String id;
    /**物资分类id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类id")
    private java.lang.String categoryId;
    /**物资编码*/
    @Excel(name="应急物资编号",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "物资编码")
    private java.lang.String materialsCode;
    /**物资名称*/
    @Excel(name="应急物资名称",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "物资名称")
    private java.lang.String materialsName;
    /**规格型号*/
    @Excel(name="规格型号",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
    /**单位*/
    @Excel(name="单位",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "单位")
    @Dict(dicCode = "materials_unit")
    private java.lang.String unit;
    /**物资分类名称*/
    @Excel(name="应急物资分类",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类名称")
    private java.lang.String categoryName;
    /**是否防汛物资(0否、1是)*/
    @Excel(name = "是否为防汛物资", width = 15)
    @ApiModelProperty(value = "是否防汛物资(0否、1是)")
    private java.lang.String floodProtection;
    /**数量*/
    @Excel(name="数量",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "数量")
    private java.lang.String number;
    /**位置名称*/
    @Excel(name = "存放位置", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "存放位置")
    private java.lang.String depositPositionName;
    /**主管部门名称*/
    @Excel(name="主管部门",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private java.lang.String primaryName;
    /**负责人名称*/
    @Excel(name="负责人",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人名称")
    private java.lang.String userName;
    /**联系电话*/
    @Excel(name="联系电话",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**错误原因*/
    @Excel(name = "错误原因", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "错误原因")
    private java.lang.String wrongReason;
    /**物资分类编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类编码")
    private java.lang.String categoryCode;
    /**主管部门编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门编码")
    private java.lang.String primaryOrg;
    /**负责人ID*/
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人ID")
    private java.lang.String userId;
    /**线路编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路编码")
    private java.lang.String lineCode;

    /**站点编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点编码")
    private java.lang.String stationCode;

    /**位置编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "位置编码")
    private java.lang.String positionCode;

    /**线路名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路名称")
    private java.lang.String lineName;

    /**站点名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点名称")
    private java.lang.String stationName;

    /**位置名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "位置名称")
    private java.lang.String positionName;






}
