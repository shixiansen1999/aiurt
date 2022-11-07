package com.aiurt.boot.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: repair_task_result
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("repair_task_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="repair_task_result对象", description="repair_task_result")
public class RepairTaskResult implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
	/**检查项编号*/
	@Excel(name = "检查项编号", width = 15)
    @ApiModelProperty(value = "检查项编号")
    private java.lang.String code;
	/**检修项名称*/
	@Excel(name = "检修项名称", width = 15)
    @ApiModelProperty(value = "检修项名称")
    private java.lang.String name;
	/**关联repair_task_device_rel表的id*/
	@Excel(name = "关联repair_task_device_rel表的id", width = 15)
    @ApiModelProperty(value = "关联repair_task_device_rel表的id")
    private java.lang.String taskDeviceRelId;
	/**维修内容*/
	@Excel(name = "维修内容", width = 15)
    @ApiModelProperty(value = "维修内容")
    private java.lang.String maintenanceContent;
	/**质量标准*/
	@Excel(name = "质量标准", width = 15)
    @ApiModelProperty(value = "质量标准")
    private java.lang.String qualityStandard;
	/**排序编号*/
	@Excel(name = "排序编号", width = 15)
    @ApiModelProperty(value = "排序编号")
    private java.lang.Integer sortNo;
    /**检修人id*/
    @Excel(name = "检修人id", width = 15)
    @ApiModelProperty(value = "检修人id")
    private java.lang.String staffId;

    /**检修人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修人名称")
    private String staffName;
	/**父级id，顶级为0*/
	@Excel(name = "父级id，顶级为0", width = 15)
    @ApiModelProperty(value = "父级id，顶级为0")
    private java.lang.String pid;
	/**是否有孩子节点（0否1是）*/
	@Excel(name = "是否有孩子节点（0否1是）", width = 15)
    @ApiModelProperty(value = "是否有孩子节点（0否1是）")
    private java.lang.String hasChild;
	/**检查项类型，是否是检查项：0否 1是*/
	@Excel(name = "检查项类型，是否是检查项：0否 1是", width = 15)
    @ApiModelProperty(value = "检查项类型，是否是检查项：0否 1是")
    private java.lang.Integer type;

    /**检查项类型名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检查项类型名称")
    private String typeName;

	/**数据字典：1 无、2 选择项、3 输入项*/
	@Excel(name = "数据字典：1无、2 选择项、3 输入项", width = 15)
    @ApiModelProperty(value = "数据字典：1 无、2 选择项、3 输入项")
    private java.lang.Integer statusItem;
	/**选择项关联的数据字典*/
	@Excel(name = "选择项关联的数据字典", width = 15)
    @ApiModelProperty(value = "选择项关联的数据字典")
    private java.lang.String dictCode;
	/**检修结果 1.正常 2.异常*/
	@Excel(name = "检修结果 1.正常 2.异常", width = 15)
    @ApiModelProperty(value = "检修结果 1.正常 2.异常")
    private java.lang.Integer status;

    /**检修结果名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检修结果名称")
    private String statusName;
	/**检测值*/
	@Excel(name = "检测值", width = 15)
    @ApiModelProperty(value = "检测值")
    private java.lang.Integer inspeciontValue;

    /**检测值是否必填*/
    @Excel(name = "检测值是否必填：0否 1是", width = 15)
    @ApiModelProperty(value = "检测值是否必填：0否 1是")
    private java.lang.Integer inspectionType;

    /**检测值是否必填名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检测值是否必填名称")
    private String inspectionTypeName;

    /**检测值名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "检测值名称")
    private String inspeciontValueName;
	/**输入项填写内容，status_item为3时此项必填*/
	@Excel(name = "输入项填写内容，status_item为3时此项必填", width = 15)
    @ApiModelProperty(value = "输入项填写内容，status_item为3时此项必填")
    private java.lang.String note;
    /**异常描述*/
    @Excel(name = "异常描述", width = 15)
    @ApiModelProperty(value = "异常描述")
    private java.lang.String unNote;
	/**数据校验字段*/
	@Excel(name = "数据校验字段", width = 15)
    @ApiModelProperty(value = "数据校验字段")
    private java.lang.String dataCheck;
	/**删除状态*/
	@Excel(name = "删除状态", width = 15)
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

    /**父级名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "父级名称")
    private java.lang.String parentName;

    /**附件url*/
    @TableField(exist = false)
    @ApiModelProperty(value = "附件url")
    private List<String> url;

    /**
     * 子节点
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "子节点")
    private List<RepairTaskResult> children;
}
