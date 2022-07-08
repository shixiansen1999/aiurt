package com.aiurt.boot.task.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/4
 * @desc
 */
@Data
public class PatrolCheckDTO {
    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
    @Excel(name = "数据填写类型：1 无、2 选择项、3 输入项", width = 15)
    @ApiModelProperty(value = "数据填写类型：1 无、2 选择项、3 输入项")
    private java.lang.Integer inputType;
    /**关联的数据字典编码*/
    @Excel(name = "关联的数据字典编码", width = 15)
    @ApiModelProperty(value = "关联的数据字典编码")
    private java.lang.String dictCode;
    /**关联的数据字典项或开关项结果值*/
    @Excel(name = "关联的数据字典项或开关项结果值", width = 15)
    @ApiModelProperty(value = "关联的数据字典项或开关项结果值")
    private java.lang.Integer optionValue;
    /**手动输入结果*/
    @Excel(name = "手动输入结果", width = 15)
    @ApiModelProperty(value = "手动输入结果")
    private java.lang.String writeValue;
    /**数据校验表达式*/
    @Excel(name = "数据校验表达式", width = 15)
    @ApiModelProperty(value = "数据校验表达式")
    private java.lang.String regular;
}
