package com.aiurt.boot.materials.entity;

import java.io.Serializable;

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
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_materials_usage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_materials_usage对象", description="emergency_materials_usage")
public class EmergencyMaterialsUsage extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**物资编号*/
    @Excel(name = "物资id", width = 15)
    @ApiModelProperty(value = "物资id")
    private java.lang.String materialsId;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private java.lang.String materialsCode;
	/**物资名称*/
	@Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private java.lang.String materialsName;

    /**物资分类名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类名称")
    private java.lang.String categoryName;

    /**物资分类名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类编码")
    private java.lang.String categoryCode;
	/**使用数量*/
	@Excel(name = "使用数量", width = 15)
    @ApiModelProperty(value = "使用数量")
    private java.lang.Integer number;
	/**使用日期*/
	@Excel(name = "使用日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "使用日期")
    private java.util.Date useDate;
	/**使用时间*/
	@Excel(name = "使用时间", width = 15, format = "HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "使用时间")
    private java.util.Date useTime;
	/**使用人ID*/
	@Excel(name = "使用人ID", width = 15)
    @ApiModelProperty(value = "使用人ID")
    private java.lang.String userId;

    /**使用人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "使用人名称")
    private java.lang.String userName;
	/**归还日期*/
	@Excel(name = "归还日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "归还日期")
    private java.util.Date backDate;
	/**归还时间*/
	@Excel(name = "归还时间", width = 15, format = "HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm")
    @DateTimeFormat(pattern="HH:mm")
    @ApiModelProperty(value = "归还时间")
    private java.util.Date backTime;
	/**归还人ID*/
	@Excel(name = "归还人ID", width = 15)
    @ApiModelProperty(value = "归还人ID")
    private java.lang.String backId;
    /**归还人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "归还人名称")
    private java.lang.String backName;
    /**提交状态(0待提交,1已提交)*/
    @Excel(name = "提交状态(0待提交,1已提交)", width = 15)
    @ApiModelProperty(value = "提交状态(0待提交,1已提交)")
    @Dict(dicCode = "usage_status")
    private java.lang.Integer status;
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
    @ApiModelProperty(value = "开始时间")
    private java.lang.String startTime;


    /**巡视日期*/
    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private java.lang.String endTime;

    /**备注*/
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;

    /**主管部门编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门编码")
    private java.lang.String primaryOrg;

    /**主管部门名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private java.lang.String primaryName;


    /**规格型号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
    /**单位*/
    @TableField(exist = false)
    @ApiModelProperty(value = "单位")
    @Dict(dicCode = "materials_unit")
    private java.lang.String unit;
}
