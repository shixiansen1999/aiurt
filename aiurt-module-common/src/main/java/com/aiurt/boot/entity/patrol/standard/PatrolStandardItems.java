package com.aiurt.boot.entity.patrol.standard;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class PatrolStandardItems implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**标准ID*/
	@Excel(name = "标准ID", width = 15)
    @ApiModelProperty(value = "标准ID")
    private java.lang.String standardId;
	/**巡检项编号*/
	@Excel(name = "巡检项编号", width = 15)
    @ApiModelProperty(value = "巡检项编号")
    private java.lang.String code;
	/**巡检项内容*/
	@Excel(name = "巡检项内容", width = 15)
    @ApiModelProperty(value = "巡检项内容")
    private java.lang.String content;
	/**质量标准*/
	@Excel(name = "质量标准", width = 15)
    @ApiModelProperty(value = "质量标准")
    private java.lang.String qualityStandard;
	/**层级类型：0一级、1子级*/
	@Excel(name = "层级类型：0一级、1子级", width = 15)
    @ApiModelProperty(value = "层级类型：0一级、1子级")
    private java.lang.Integer hierarchyType;
	/**父级ID，其中顶级为0*/
	@Excel(name = "父级ID，其中顶级为0", width = 15)
    @ApiModelProperty(value = "父级ID，其中顶级为0")
    private java.lang.String parentId;
	/**内容排序*/
	@Excel(name = "内容排序", width = 15)
    @ApiModelProperty(value = "内容排序")
    private java.lang.Integer order;
	/**是否为巡检项目：0否 1是*/
	@Excel(name = "是否为巡检项目：0否 1是", width = 15)
    @ApiModelProperty(value = "是否为巡检项目：0否 1是")
    private java.lang.Integer check;
	/**数据填写类型：1开关项(即二选一)、2选择项、3输入项*/
	@Excel(name = "数据填写类型：1开关项(即二选一)、2选择项、3输入项", width = 15)
    @ApiModelProperty(value = "数据填写类型：1开关项(即二选一)、2选择项、3输入项")
    private java.lang.Integer inputType;
	/**选择项关联的数据字典code*/
	@Excel(name = "选择项关联的数据字典code", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典code")
    private java.lang.String dictCode;
	/**数据校验表达式*/
	@Excel(name = "数据校验表达式", width = 15)
    @ApiModelProperty(value = "数据校验表达式")
    private java.lang.Integer regular;
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
}
