package com.aiurt.boot.materials.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.PatrolStandardItemsModel;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.List;

@Data
public class MaterialAccountDTO {

    /**物资id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资id")
    private java.lang.String id;

    /**物资分类id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类id")
    private java.lang.String categoryId;
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "序号")
    private java.lang.Integer orderNumber;
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

    @ApiModelProperty(value = "父级ID，第一级默认为0")
    @TableField(exist = false)
    private java.lang.String pid;

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
    /**数量*/
    /**是否防汛物资(0否、1是)*/
    @Excel(name = "是否为防汛物资", width = 15,dicCode = "flood_protection")
    @ApiModelProperty(value = "是否防汛物资(0否、1是)")
    @Dict(dicCode = "flood_protection")
    private java.lang.Integer floodProtection;
    @Excel(name="数量",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer number;
    /**主管部门名称*/
    @Excel(name="主管部门",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private java.lang.String primaryName;
  /**主管部门名称*/
    @Excel(name="主管部门",width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private List<String> orgCodeList;

    /**主管部门集合*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门集合")
    private List<String> primaryCodeList;

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

    /**巡检标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "导出传入ids")
    private List<String> selections;

    /**巡检标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检标准id")
    private java.lang.String patrolStandardId;

    /**巡检项*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检项")
    private List<PatrolStandardItemsModel> patrolStandardItemsModelList;

    /**巡检项*/
    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<MaterialAccountDTO> children;

    /**应急预案标识*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急预案标识")
    private java.lang.String planFlag;
}
