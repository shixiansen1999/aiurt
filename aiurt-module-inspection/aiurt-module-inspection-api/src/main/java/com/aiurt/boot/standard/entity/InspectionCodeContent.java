package com.aiurt.boot.standard.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: inspection_code_content
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("inspection_code_content")
@ApiModel(value="inspection_code_content对象", description="inspection_code_content")
public class InspectionCodeContent implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;

    /**是否有子节点*/
    @Excel(name = "层级类型", width = 15)
    @ExcelExtend(isRequired = true)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;

    /**父级节点，顶级为0*/
    @Excel(name = "父级", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "父级节点，顶级为0")
    private java.lang.String pid;


    /**检修项名称*/
    @Excel(name = "检修项内容", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修项名称")
    private java.lang.String name;

	/**检查项编号*/
	@Excel(name = "检修项编号", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检查项编号")
    private java.lang.String code;

    /**排序编号*/
    @ApiModelProperty(value = "排序编号")
    private java.lang.Integer sortNo;

    /**排序编号*/
    @Excel(name = "内容排序", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "排序编号")
    @TableField(exist = false)
    private java.lang.String isSortNo;

    /**检查项类型，是否是检查项：0否 1是*/
    @ApiModelProperty(value = "检查项类型，是否是检查项：0否 1是")
    @Dict(dicCode = "inspection_value")
    private java.lang.Integer type;

    /**检查项类型，是否是检查项：0否 1是*/
    @Excel(name = "是否为检查项", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检查项类型，是否是检查项：0否 1是")
    @TableField(exist = false)
    private java.lang.String isType;

    /**质量标准*/
    @Excel(name = "质量标准", width = 15)
    @ApiModelProperty(value = "质量标准")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String qualityStandard;


    /**数据字典：1 无、2 选择项、3 输入项*/
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "数据字典：1 无、2 选择项、3 输入项")
    private java.lang.Integer statusItem;

    /**数据字典：1 无、2 选择项、3 输入项*/
    @Excel(name = "检查值类型", width = 15)
    @ExcelExtend(isRequired = true)
    @TableField(updateStrategy = FieldStrategy.IGNORED,exist = false)
    @ApiModelProperty(value = "数据字典：1 无、2 选择项、3 输入项")
    @Dict(dicCode = "patrol_input_type")
    private java.lang.String sStatusItem;

    /**
     * 检查值是否必填：0否1是
     */
    @ApiModelProperty(value = "检查值是否必填：0否 1是")
    @Dict(dicCode = "inspection_value")
    private java.lang.Integer inspectionType;

    /**
     * 检查值是否必填：0否1是
     */
    @Excel(name = "检查值是否必填", width = 15)
    @ApiModelProperty(value = "检查值是否必填：0否 1是")
//    @Dict(dicCode = "inspection_value")
    @TableField(exist = false)
    private java.lang.String isInspectionType;


	/**选择项关联的数据字典*/
	@Excel(name = "关联数据字典", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典")
    private java.lang.String dictCode;
	/**数据校验字段*/
	@Excel(name = "数据校验表达式", width = 15)
    @ApiModelProperty(value = "数据校验字段")
    private java.lang.String dataCheck;

    /**检修标准id，关联inspection_code表的id*/
    @Excel(name = "检修标准id", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修标准id，关联inspection_code表的id")
    private java.lang.String inspectionCodeId;

	/**删除状态*/
    @ApiModelProperty(value = "删除状态")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private java.lang.String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;

    /**
     * 子节点
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<InspectionCodeContent> children;

    @TableField(exist = false)
    @ApiModelProperty(value = "检查项类型名称")
    private java.lang.String typeName;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据填写类型")
    private java.lang.String statusItemName;

    @TableField(exist = false)
    @ApiModelProperty(value = "检查值是否必填名称")
    private java.lang.String inspectionTypeName;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private String codeContentErrorReason;

    /**数据是否是空*/
    @ApiModelProperty(value = "数据是否为null")
    @TableField(exist = false)
    private  Boolean  isNUll;
}
