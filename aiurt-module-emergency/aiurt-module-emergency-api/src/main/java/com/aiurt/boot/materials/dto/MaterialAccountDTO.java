package com.aiurt.boot.materials.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    /**联系电话*/
    @TableField(exist = false)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;

    /**存放位置编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "存放位置编码")
    private java.lang.String location;


    /**线路名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路名称")
    private java.lang.String lineName;


}
