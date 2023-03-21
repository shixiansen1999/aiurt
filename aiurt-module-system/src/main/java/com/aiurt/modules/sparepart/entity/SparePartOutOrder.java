package com.aiurt.modules.sparepart.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
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
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Data
@TableName("spare_part_out_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_out_order对象", description="spare_part_out_order")
public class SparePartOutOrder implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;
    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private String name;
	/**出库的仓库编码*/
	@Excel(name = "出库的仓库编码", width = 15)
    @ApiModelProperty(value = "出库的仓库编码")
    private String warehouseCode;
    /**出库的仓库名称*/
    @Excel(name = "出库的仓库名称", width = 15)
    @ApiModelProperty(value = "出库的仓库名称")
    @TableField(exist = false)
    private String warehouseName;
	/**出库数量*/
	@Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private Integer num;
	/**确认时间*/
	@Excel(name = "确认时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;
	/**确认人ID*/
	@Excel(name = "确认人ID", width = 15)
    @ApiModelProperty(value = "确认人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String confirmUserId;
    /**确认人*/
    @Excel(name = "确认人", width = 15)
    @ApiModelProperty(value = "确认人")
    @TableField(exist = false)
    private String confirmName;
	/**申请出库时间*/
	@Excel(name = "申请出库时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "申请出库时间")
    private Date applyOutTime;
	/**申请出库人ID*/
	@Excel(name = "申请出库人ID", width = 15)
    @ApiModelProperty(value = "申请出库人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String applyUserId;
    /**申请出库人*/
    @Excel(name = "申请出库人", width = 15)
    @ApiModelProperty(value = "申请出库人")
    @TableField(exist = false)
    private String applyUserName;
	/**备件出库单状态：1待确认、2已确认*/
	@Excel(name = "备件出库单状态：1待确认、2已确认", width = 15)
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    @Dict(dicCode = "spare_out_order_status")
    private Integer status;
    /**出库单状态名称*/
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    @Dict(dicCode = "spare_out_order_status")
    @TableField(exist = false)
    private String statusName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**已出库剩余数量*/
	@Excel(name = "已出库剩余数量", width = 15)
    @ApiModelProperty(value = "已出库剩余数量")
    private String unused;
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
    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private  Integer  type;
    /**物资类型名称*/
    @Excel(name = "物资类型", width = 15)
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
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;
    /**备件退库、报废列表拼接条件*/
    @ApiModelProperty(value = " 报废列表拼接条件")
    @TableField(exist = false)
    private String orgId;
    /**app出库查询*/
    @ApiModelProperty(value = "app出库查询")
    @TableField(exist = false)
    private String text;
}
