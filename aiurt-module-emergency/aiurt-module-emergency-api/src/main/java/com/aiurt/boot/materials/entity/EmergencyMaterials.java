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
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Data
@TableName("emergency_materials")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="emergency_materials对象", description="emergency_materials")
public class EmergencyMaterials extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**应急物资编号*/
	@Excel(name = "应急物资编号", width = 15)
    @ApiModelProperty(value = "应急物资编号")
    private java.lang.String materialsCode;
	/**应急物资名称*/
	@Excel(name = "应急物资名称", width = 15)
    @ApiModelProperty(value = "应急物资名称")
    private java.lang.String materialsName;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    @Dict(dicCode = "materials_unit")
    private java.lang.String unit;
	/**物资分类编码*/
	@Excel(name = "物资分类编码", width = 15)
    @ApiModelProperty(value = "物资分类编码")
    private java.lang.String categoryCode;
    /**物资分类名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资分类名称")
    private java.lang.String categoryName;
	/**是否防汛物资(0否、1是)*/
	@Excel(name = "是否防汛物资(0否、1是)", width = 15)
    @ApiModelProperty(value = "是否防汛物资(0否、1是)")
    @Dict(dicCode = "flood_protection")
    private java.lang.Integer floodProtection;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer number;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**线路编码*/
	@Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    private java.lang.String lineCode;
    /**站点编码*/
    @Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    private java.lang.String stationCode;
    /**位置编码*/
    @Excel(name = "位置编码", width = 15)
    @ApiModelProperty(value = "位置编码")
    private java.lang.String positionCode;
	/**主管部门编码*/
	@Excel(name = "主管部门编码", width = 15)
    @ApiModelProperty(value = "主管部门编码")
    private java.lang.String primaryOrg;
    /**主管部门名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主管部门名称")
    private java.lang.String primaryName;
	/**负责人ID*/
	@Excel(name = "负责人ID", width = 15)
    @ApiModelProperty(value = "负责人ID")
    private java.lang.String userId;

    /**负责人名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "负责人名称")
    private java.lang.String userName;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
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

    /**线路名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路名称")
    private java.lang.String lineName;

    /**站点名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点名称")
    private java.lang.String stationName;

    /**位置名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "位置名称")
    private java.lang.String positionName;
}
