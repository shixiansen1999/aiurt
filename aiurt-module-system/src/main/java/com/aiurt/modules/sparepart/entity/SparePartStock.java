package com.aiurt.modules.sparepart.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
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
import java.util.Date;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Data
@TableName("spare_part_stock")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_stock对象", description="spare_part_stock")
public class SparePartStock implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Integer num;
	/**仓库编号*/
	@Excel(name = "仓库编号", width = 15)
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**组织机构id*/
	@Excel(name = "组织机构id", width = 15)
    @ApiModelProperty(value = "组织机构id")
    private String orgId;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**仓库名称*/
    @Excel(name = "仓库名称", width = 15)
    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
    /**所属专业code*/
    @ApiModelProperty(value = "专业名称code")
    @TableField(exist = false)
    @MajorFilterColumn
    private  String  majorCode;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private  String  majorName;
    /**子系统code*/
    @ApiModelProperty(value = "子系统code")
    @TableField(exist = false)
    @SystemFilterColumn
    private  String  systemCode;
    /**子系统名称*/
    @Excel(name = "所属子系统", width = 15)
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private  String  systemName;
    /**物资分类code*/
    @ApiModelProperty(value = "物资分类code")
    @TableField(exist = false)
    private  String  baseTypeCode;
    /**物资分类*/
    @Excel(name = "物资分类", width = 15)
    @ApiModelProperty(value = "物资分类名称")
    @TableField(exist = false)
    private  String  baseTypeCodeName;
    /**物资类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private  Integer  type;
    /**最低库存值*/
    @Excel(name = "最低库存值", width = 15)
    @ApiModelProperty(value = "最低库存值")
    private  Integer  minimumStock;
    /**物资类型名称*/
    @Excel(name = "物资类型名称", width = 15)
    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private  String  typeName;
    /**班组名称*/
    @Excel(name = "班组名称", width = 15)
    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private  String  orgName;
    /**规格型号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "规格型号")
    private String specifications;
    /**单位*/
    @ApiModelProperty(value = " 单位")
    @TableField(exist = false)
    private String unit;
    /**生产厂商*/
    @ApiModelProperty(value = "生产厂商名称")
    @TableField(exist = false)
    private String manufactorCodeName;
    /**单价(元)*/
    @ApiModelProperty(value = " 单价")
    @TableField(exist = false)
    private String price;
    /**当前模块*/
    @ApiModelProperty(value = "当前模块")
    @TableField(exist = false)
    private String module;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;
    /**是否是易耗品：1是，0不是*/
    @ApiModelProperty(value = "是否是易耗品：1是，0不是")
    @TableField(exist = false)
    private Integer consumablesType;
}
