package com.aiurt.boot.standard.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: patrol_standard_items
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_standard_items")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_standard_items对象", description="patrol_standard_items")
public class PatrolStandardItemsExport implements Serializable {
    private static final long serialVersionUID = 1L;
    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
    /**标准ID*/
    @ApiModelProperty(value = "标准ID")
    private java.lang.String standardId;
    /**层级类型：0一级、1子级*/
    @ApiModelProperty(value = "层级类型：0一级、1子级")
    private java.lang.Integer hierarchyType;
    /**层级类型：0一级、1子级*/
    @Excel(name = "层级类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "层级类型：0一级、1子级")
    @TableField(exist = false)
    private java.lang.String hierarchyTypeName;
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
    @Excel(name = "内容排序", width = 15,needMerge = true)
    @ApiModelProperty(value = "内容排序")
    @TableField(value = "`order`")
    private java.lang.Integer order;
    /**是否为巡检项目：0否 1是*/
    @ApiModelProperty(value = "是否为巡检项目：0否 1是")
    @TableField(value = "`check`")
    private java.lang.Integer check;
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
    /**父级ID，其中顶级为0*/
    @ApiModelProperty(value = "父级ID，其中顶级为0")
    private java.lang.String parentId;
    /**数据填写类型：1开关项(即二选一)、2选择项、3输入项*/
    //@Excel(name = "数据填写类型：1开关项(即二选一)、2选择项、3输入项", width = 15)
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "数据填写类型：1开关项(即二选一)、2选择项、3输入项")
    private java.lang.Integer inputType;
    /**选择项关联的数据字典code*/
    //@Excel(name = "选择项关联的数据字典code", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典code")
    private java.lang.String dictCode;
    /**数据校验表达式*/
    //@Excel(name = "数据校验表达式", width = 15)
    @ApiModelProperty(value = "数据校验表达式")
    private java.lang.String regular;
    /**检查值是否必填：0否、1是*/
    //@Excel(name = "检查值是否必填：0否、1是", width = 15)
    @ApiModelProperty(value = "检查值是否必填：0否、1是")
    private java.lang.Integer required;
    /**检查值是否必填字典名：0否、1是*/
    //@Excel(name = "检查值是否必填字典名：0否、1是", width = 15)
    @ApiModelProperty(value = "检查值是否必填字典名：0否、1是")
    @TableField(exist = false)
    private java.lang.String requiredDictName;
    /**删除状态： 0未删除 1已删除*/
    //@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
