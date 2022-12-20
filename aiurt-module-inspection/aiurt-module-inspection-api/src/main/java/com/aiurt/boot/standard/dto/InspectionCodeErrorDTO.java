package com.aiurt.boot.standard.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/12/19
 * @time: 16:25
 */

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.standard.entity.InspectionCodeContent;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.system.base.annotation.ExcelExtend;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-12-19 16:25
 */
@Data
public class InspectionCodeErrorDTO {
    /**检修标准名称*/
    @Excel(name = "检修标准表名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "检修标准名称")
    private String title;
    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @Dict(dicCode = "inspection_cycle_type")
    private Integer type;

    /**检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)*/
    @Excel(name = "检修周期类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "检修周期类型(0周检、1月检、2双月检、3季检、4半年检、5年检)")
    @TableField(exist = false)
    private String cycleType;

    /**专业code,关联cs_major的code*/
    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code,关联cs_major的code")
    private String majorCode;

    /**专业子系统code,关联cs_subsystem_user的code*/
    @Excel(name = "适用子系统", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业子系统code,关联cs_subsystem_user的code")
    private String subsystemCode;

    /**是否与设备相关(0否1是)*/
    @Excel(name = "与设备类型相关", width = 15,needMerge = true)
    @ApiModelProperty(value = "是否与设备相关(0否1是)")
    @TableField(exist = false)
    private String isRelatedDevice;

    /**设备类型code，关联device_type的code*/
    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code，关联device_type的code")
    @TableField(exist = false)
    private String deviceTypeName;

    /**状态 0-未生效 1-已生效*/
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "状态 0-未生效 1-已生效")
    @TableField(exist = false)
    private String effectStatus;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  standMistake;



    /**是否有子节点*/
    @Excel(name = "是否有子节点", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;

    /**父级节点，顶级为0*/
    @Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点，顶级为0")
    private java.lang.String pid;


    /**检修项名称*/
    @Excel(name = "检修项内容", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检修项名称")
    private java.lang.String name;

    /**检查项编号*/
    @Excel(name = "检查项编号", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "检查项编号")
    private java.lang.String code;

    /**排序编号*/
    @ApiModelProperty(value = "排序编号")
    private java.lang.Integer sortNo;

    /**排序编号*/
    @Excel(name = "排序编号", width = 15)
    @ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "排序编号")
    @TableField(exist = false)
    private java.lang.String isSortNo;


    /**检查项类型，是否是检查项：0否 1是*/
    @Excel(name = "是否是检查项", width = 15)
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
    @TableField(exist = false)
    private java.lang.String isInspectionType;


    /**选择项关联的数据字典*/
    @Excel(name = "选择项关联的数据字典", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典")
    private java.lang.String dictCode;
    /**数据校验字段*/
    @Excel(name = "数据校验字段", width = 15)
    @ApiModelProperty(value = "数据校验字段")
    private java.lang.String dataCheck;

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
    private  String  itemParentMistake;
}
