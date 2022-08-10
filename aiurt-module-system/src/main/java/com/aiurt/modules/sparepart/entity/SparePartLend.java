package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
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
 * @Description: spare_part_lend
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Data
@TableName("spare_part_lend")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_lend对象", description="spare_part_lend")
public class SparePartLend implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**ids*/
    @ApiModelProperty(value = "ids")
    @TableField(exist = false)
    private List<String> ids;
    /**序号*/
    @Excel(name = "序号", width = 15)
    @TableField(exist = false)
    private String number;
    /**状态（	1：待借出、2：已借、3：待确认、4：已完结）*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态（1：待借出、2：已借、3：待确认、4：已完结）")
    @Dict(dicCode = "spare_lend_status")
    private Integer status;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
    /**借入数量*/
    @Excel(name = "借入数量", width = 15)
    @ApiModelProperty(value = "借入数量")
    private Integer borrowNum;
    /**申请借入时间*/
    @Excel(name = "申请借入时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请借入时间")
    private Date createTime;
    /**借入仓库名称*/
    @Excel(name = "借入仓库", width = 15)
    @ApiModelProperty(value = "借入仓库")
    @TableField(exist = false)
    private String backWarehouseName;
    /**借用人*/
    @Excel(name = "借用人", width = 15)
    @ApiModelProperty(value = "借用人")
    @TableField(exist = false)
    private String lendPersonName;
    /**借出数量*/
    @Excel(name = "借出数量", width = 15)
    @ApiModelProperty(value = "借出数量")
    private Integer lendNum;
    /**借出时间*/
    @Excel(name = "借出时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "借出时间")
    private Date outTime;
    /**借出仓库名称*/
    @Excel(name = "借出仓库", width = 15)
    @ApiModelProperty(value = "借出仓库名称")
    @TableField(exist = false)
    private String lendWarehouseName;
    /**归还数量*/
    @Excel(name = "归还数量", width = 15)
    @ApiModelProperty(value = "归还数量")
    private Integer backNum;
    /**归还时间*/
    @Excel(name = "归还时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "归还时间")
    private Date backTime;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;

	/**借出仓库编号*/
    @ApiModelProperty(value = "借出仓库编号")
    private String lendWarehouseCode;
	/**借入仓库编号*/
    @ApiModelProperty(value = "借入仓库编号")
    private String backWarehouseCode;
    /**借用人ID*/
    @ApiModelProperty(value = "借用人ID")
    private String lendPerson;
	/**归还人ID*/
    @ApiModelProperty(value = "归还人ID")
    private String backPerson;
    /**归还人*/
    @ApiModelProperty(value = "归还人")
    @TableField(exist = false)
    private String backPersonName;
	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**所属专业code*/
    @ApiModelProperty(value = "专业名称code")
    @TableField(exist = false)
    @MajorFilterColumn
    private  String  majorCode;
    /**所属专业*/
    @ApiModelProperty(value = "专业名称")
    @TableField(exist = false)
    private  String  majorName;
    /**子系统code*/
    @ApiModelProperty(value = "子系统code")
    @TableField(exist = false)
    @SystemFilterColumn
    private  String  systemCode;
    /**子系统名称*/
    @ApiModelProperty(value = "子系统名称")
    @TableField(exist = false)
    private  String  systemName;
    /**物资分类code*/
    @ApiModelProperty(value = "物资分类code")
    @TableField(exist = false)
    private  String  baseTypeCode;
    /**物资分类*/
    @ApiModelProperty(value = "物资分类名称")
    @TableField(exist = false)
    private  String  baseTypeCodeName;
    /**物资类型*/
    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private  Integer  type;
    /**物资类型名称*/
    @ApiModelProperty(value = "物资类型名称")
    @TableField(exist = false)
    private  String  typeName;
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
    /**申请借入部门*/
    @ApiModelProperty(value = " 申请借入部门")
    @TableField(exist = false)
    private String borrowName;
    /**借出部门*/
    @ApiModelProperty(value = " 借出部门")
    @TableField(exist = false)
    private String lendName;
    /**归还部门*/
    @ApiModelProperty(value = " 归还部门")
    @TableField(exist = false)
    private String returnName;
    /**创建人的组织机构编码*/
    @ApiModelProperty(value = "创建人的组织机构编码")
    @DeptFilterColumn
    private String createOrgCode;
    /**借入的组织机构编码*/
    @ApiModelProperty(value = "借入的组织机构编码")
    @DeptFilterColumn
    private String entryOrgCode;
    /**借出的组织机构编码*/
    @ApiModelProperty(value = "借出的组织机构编码")
    @DeptFilterColumn
    private String exitOrgCode;
}
