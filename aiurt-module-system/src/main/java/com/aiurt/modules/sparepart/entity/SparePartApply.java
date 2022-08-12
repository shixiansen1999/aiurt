package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.stock.entity.StockLevel2;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
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
 * @Description: spare_part_apply
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
@Data
@TableName("spare_part_apply")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_apply对象", description="spare_part_apply")
public class SparePartApply extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;
    /**申领状态：1待提交、2待确认、3已确认*/
    @Excel(name = "申领状态：1待提交、2待确认、3已确认", width = 15)
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认")
    @Dict(dicCode = "spare_apply_status")
    private String status;
	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
    @ApiModelProperty(value = "申领编号")
    private String code;
    /**申领仓库*/
    @Excel(name = "申领仓库", width = 15)
    @ApiModelProperty(value = "申领仓库")
    @TableField(exist = false)
    private  String  applyWarehouse;
    /**保管仓库*/
    @Excel(name = "保管仓库", width = 15)
    @ApiModelProperty(value = "保管仓库")
    @TableField(exist = false)
    private  String  custodialWarehouse;
    /**申领数量*/
    @Excel(name = "申领数量", width = 15)
    @ApiModelProperty(value = "申领数量")
    private Integer applyNumber;

	/**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String applyWarehouseCode;

	/**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    @Dict(dictTable ="spare_part_stock_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String custodialWarehouseCode;
	/**保管仓库名称*/
    @ApiModelProperty(value = "保管仓库名称")
    @TableField(exist = false)
    private String warehouseName;
    @Excel(name = "申领人", width = 15)
    @ApiModelProperty("申领人")
    @TableField(exist = false)
    private String applyUser;
    /**申领时间*/
    @Excel(name = "申领时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "申领时间")
    private Date applyTime;

	/**申领人ID*/
    @ApiModelProperty(value = "申领人ID")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String applyUserId;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**班组*/
    @ApiModelProperty(value = "班组")
    private String departId;
	/**出库仓库 二级库*/
    @ApiModelProperty(value = "出库仓库 二级库")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String outWarehouseCode;
	/**提交状态（0-未提交 1-已提交）*/
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")
    private Integer commitStatus;
	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String updateBy;

	/**出库时间*/
	@Excel(name = "出库时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出库时间")
    private Date stockOutTime;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**物资*/
    @ApiModelProperty(value = "物资")
    @TableField(exist = false)
    private List<SparePartApplyMaterial> stockLevel2List;
    /**物资(二级库出库）*/
    @ApiModelProperty(value = "物资(二级库出库）")
    @TableField(exist = false)
    private List<StockOutboundMaterials> stockOutboundMaterialsList;
    /**出库合计*/
    @ApiModelProperty(value = "物资出库合计")
    @TableField(exist = false)
    private Integer totalCount;
    /**出库操作用户ID*/
    @ApiModelProperty(value = "出库操作用户ID")
    @TableField(exist = false)
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
    private  String  userId;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "出库时间")
    @TableField(exist = false)
    private  java.util.Date  outTime;
    /**出库单号*/
    @ApiModelProperty(value = "出库单号")
    @TableField(exist = false)
    private  String  orderCode;
    @ApiModelProperty(value = "出库单备注")
    @TableField(exist = false)
    private  String  outOrderRemark;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @DeptFilterColumn
    private String sysOrgCode;


}
