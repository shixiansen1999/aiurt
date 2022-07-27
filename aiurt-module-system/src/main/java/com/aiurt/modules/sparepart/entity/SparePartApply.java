package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.stock.entity.StockLevel2;
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
	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
    @ApiModelProperty(value = "申领编号")
    private String code;
	/**申领仓库编号*/
	@Excel(name = "申领仓库编号", width = 15)
    @ApiModelProperty(value = "申领仓库编号")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String applyWarehouseCode;
	/**申领数量*/
	@Excel(name = "申领数量", width = 15)
    @ApiModelProperty(value = "申领数量")
    private Integer applyNumber;
	/**保管仓库编号*/
	@Excel(name = "保管仓库编号", width = 15)
    @ApiModelProperty(value = "保管仓库编号")
    @Dict(dictTable ="spare_part_stock_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private String custodialWarehouseCode;
	/**申领人ID*/
	@Excel(name = "申领人ID", width = 15)
    @ApiModelProperty(value = "申领人ID")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String applyUserId;
	/**申领状态：1待提交、2待确认、3已确认*/
	@Excel(name = "申领状态：1待提交、2待确认、3已确认", width = 15)
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认")
    @Dict(dicCode = "spare_apply_status")
    private Integer status;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
	/**班组*/
	@Excel(name = "班组", width = 15)
    @ApiModelProperty(value = "班组")
    private String departId;
	/**出库仓库 二级库*/
	@Excel(name = "出库仓库 二级库", width = 15)
    @ApiModelProperty(value = "出库仓库 二级库")
    private String outWarehouseCode;
	/**提交状态（0-未提交 1-已提交）*/
	@Excel(name = "提交状态（0-未提交 1-已提交）", width = 15)
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")
    private Integer commitStatus;
	/**删除状态(0.未删除 1.已删除)*/
	@Excel(name = "删除状态(0.未删除 1.已删除)", width = 15)
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
	/**申领时间*/
	@Excel(name = "申领时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "申领时间")
    private Date applyTime;
	/**出库时间*/
	@Excel(name = "出库时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出库时间")
    private Date stockOutTime;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;
    /**物资*/
    @ApiModelProperty(value = "物资")
    @TableField(exist = false)
    private List<SparePartApplyMaterial> stockLevel2List;
}
