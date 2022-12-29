package com.aiurt.boot.materials.entity;

import java.io.Serializable;
import java.util.List;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_materials_invoices_item")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_materials_invoices_item对象", description="emergency_materials_invoices_item")
public class EmergencyMaterialsInvoicesItem extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**应急物资巡检单ID*/
	@Excel(name = "应急物资巡检单ID", width = 15)
    @ApiModelProperty(value = "应急物资巡检单ID")
    private java.lang.String invoicesId;
	/**应急物资分类编码*/
	@Excel(name = "应急物资分类编码", width = 15)
    @ApiModelProperty(value = "应急物资分类编码")
    private java.lang.String categoryCode;
    /**应急物资分类名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急物资分类名称")
    private java.lang.String categoryName;
	/**应急物资编码*/
	@Excel(name = "应急物资编码", width = 15)
    @ApiModelProperty(value = "应急物资编码")
    private java.lang.String materialsCode;
	/**应急物资名称*/
	@Excel(name = "应急物资名称", width = 15)
    @ApiModelProperty(value = "应急物资名称")
    private java.lang.String materialsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer number;
	/**异常情况记录*/
	@Excel(name = "异常情况记录", width = 15)
    @ApiModelProperty(value = "异常情况记录")
    private java.lang.String abnormalCondition;
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
    @Dict(dicCode = "hierarchy_type")
    private java.lang.Integer hierarchyType;
	/**父级ID,顶级默认为0*/
	@Excel(name = "父级ID,顶级默认为0", width = 15)
    @ApiModelProperty(value = "父级ID,顶级默认为0")
    private java.lang.String pid;
	/**是否为巡检项目：0否 1是*/
	@Excel(name = "是否为巡检项目：0否 1是", width = 15)
    @ApiModelProperty(value = "是否为巡检项目：0否 1是")
    @Dict(dicCode = "check")
    @TableField(value = "`check`")
    private java.lang.Integer check;
	/**检查结果：0异常、1正常*/
	@Excel(name = "检查结果：0异常、1正常", width = 15)
    @ApiModelProperty(value = "检查结果：0异常、1正常")
    @Dict(dicCode = "check_result")
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
    private java.lang.String regular;
	/**检查值是否必填：0否、1是*/
	@Excel(name = "检查值是否必填：0否、1是", width = 15)
    @ApiModelProperty(value = "检查值是否必填：0否、1是")
    private java.lang.Integer required;
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


    /**巡视日期*/
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "巡视日期")
    private java.util.Date patrolDate;

    /**巡视人ID*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视人ID")
    private java.lang.String patrolId;

    /**巡视人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视人名称")
    private java.lang.String patrolName;

    /**巡视班组名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视班组名称")
    private java.lang.String patrolTeamName;

    /**巡视班组编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视班组编码")
    private java.lang.String patrolTeamCode;

    /**应急物资巡检单号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "应急物资巡检单号")
    private java.lang.String materialsPatrolCode;

    /**线路编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视线路编码")
    private java.lang.String lineCode;

    /**巡视线路名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视线路名称")
    private java.lang.String lineName;
    /**站点编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视站点编码")
    private java.lang.String stationCode;

    /**巡视站点名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视站点名称")
    private java.lang.String stationName;
    /**位置编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视位置编码")
    private java.lang.String positionCode;
    /**巡视位置名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "巡视位置名称")
    private java.lang.String positionName;

    /**巡视日期*/
    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String startTime;


    /**巡视日期*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private String endTime;


    /**子级*/
    @TableField(exist = false)
    @ApiModelProperty(value = "子级")
    private List<EmergencyMaterialsInvoicesItem> subLevel;

}
