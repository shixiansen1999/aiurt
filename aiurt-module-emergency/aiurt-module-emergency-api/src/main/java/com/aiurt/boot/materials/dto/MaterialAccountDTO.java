package com.aiurt.boot.materials.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.system.vo.PatrolStandardItemsModel;

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

    /**物资名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资名称")
    private java.lang.String materialsName;

    /**物资编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资编码")
    private java.lang.String materialsCode;

    /**物资分类名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类名称")
    private java.lang.String categoryName;

    /**物资分类编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类编码")
    private java.lang.String categoryCode;

    /**规格型号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;

    /**数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer number;

    /**主管部门编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门编码")
    private java.lang.String primaryOrg;

    /**主管部门名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private java.lang.String primaryName;

    /**负责人ID*/
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人ID")
    private java.lang.String userId;

    /**负责人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人名称")
    private java.lang.String userName;

    /**联系电话*/
    @TableField(exist = false)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;

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

    /**单位*/
    @TableField(exist = false)
    @ApiModelProperty(value = "单位")
    @Dict(dicCode = "materials_unit")
    private java.lang.String unit;

    /**巡检标准id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检标准id")
    private java.lang.String patrolStandardId;

    /**巡检项*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡检项")
    private List<PatrolStandardItemsModel> patrolStandardItemsModelList;


}
