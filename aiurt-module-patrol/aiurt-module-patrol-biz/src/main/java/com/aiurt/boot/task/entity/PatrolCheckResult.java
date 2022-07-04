package com.aiurt.boot.task.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: patrol_check_result
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Data
@TableName("patrol_check_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="patrol_check_result对象", description="patrol_check_result")
public class PatrolCheckResult implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键ID")
    private java.lang.String id;
	/**巡检任务标准关联表ID*/
	@Excel(name = "巡检任务标准关联表ID", width = 15)
    @ApiModelProperty(value = "巡检任务标准关联表ID")
    private java.lang.String taskStandardId;
	/**巡检任务设备关联表ID*/
	@Excel(name = "巡检任务设备关联表ID", width = 15)
    @ApiModelProperty(value = "巡检任务设备关联表ID")
    private java.lang.String taskDeviceId;
	/**检查项编号*/
	@Excel(name = "检查项编号", width = 15)
    @ApiModelProperty(value = "检查项编号")
    @TableField(value = "`code`")
    private java.lang.String code;
	/**检查项内容*/
	@Excel(name = "检查项内容", width = 15)
    @ApiModelProperty(value = "检查项内容")
    private java.lang.String content;
    /**质量标准*/
    @Excel(name = "质量标准", width = 15)
    @ApiModelProperty(value = "质量标准")
    private java.lang.String qualityStandard;
	/**层级类型：0一级、1子级*/
	@Excel(name = "层级类型：0一级、1子级", width = 15)
    @ApiModelProperty(value = "层级类型：0一级、1子级")
    private java.lang.Integer hierarchyType;
    /**原标准项目表ID*/
    @Excel(name = "原标准项目表ID", width = 15)
    @ApiModelProperty(value = "原标准项目表ID")
    private java.lang.String oldId;
	/**父级ID,顶级默认为0*/
	@Excel(name = "父级ID,顶级默认为0", width = 15)
    @ApiModelProperty(value = "父级ID,顶级默认为0")
    private java.lang.String parentId;
	/**内容排序*/
	@Excel(name = "内容排序", width = 15)
    @ApiModelProperty(value = "内容排序")
    @TableField(value = "`order`")
    private java.lang.Integer order;
    /**是否为巡检项目：0否、1是*/
    @Excel(name = "是否为巡检项目：0否、1是", width = 15)
    @ApiModelProperty(value = "是否为巡检项目：0否、1是")
    @TableField(value = "`check`")
    private java.lang.Integer check;
    /**检查结果：0异常、1正常*/
    @Excel(name = "检查结果：0异常、1正常", width = 15)
    @ApiModelProperty(value = "检查结果：0异常、1正常")
    private java.lang.Integer checkResult;
	/**数据填写类型：1 无、2 选择项、3 输入项*/
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
    private java.lang.Integer regular;
    /**检查用户ID*/
    @Excel(name = "检查用户ID", width = 15)
    @ApiModelProperty(value = "检查用户ID")
    private java.lang.String userId;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
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
    /**子节点*/
    @ApiModelProperty(value = "子节点")
    @TableField(exist = false)
    private List<PatrolCheckResult> child = new ArrayList<>();
}
