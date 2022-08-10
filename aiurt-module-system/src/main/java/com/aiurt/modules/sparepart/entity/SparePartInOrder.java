package com.aiurt.modules.sparepart.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
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
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Data
@TableName("spare_part_in_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="spare_part_in_order对象", description="spare_part_in_order")
public class SparePartInOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
	@ApiModelProperty(value = "主键ID")
	private String id;
	/**序号*/
	@Excel(name = "序号", width = 15)
	@TableField(exist = false)
	private String number;
	/**用于批量入库时，查询是否为“已确认”*/
	@ApiModelProperty(value = "用于批量入库时，查询是否为“已确认”")
	@TableField(exist = false)
	private String status;
	/**入库单状态：0-未确认 1-已确认*/
	@ApiModelProperty(value = "入库单状态：0-未确认 1-已确认")
	@Dict(dicCode = "spare_in_order_status")
	private String confirmStatus;
	/**入库单状态名称*/
	@Excel(name = "状态", width = 15)
	@ApiModelProperty(value = "入库单状态名称")
	@TableField(exist = false)
	private String confirmStatusName;
	/**所属专业*/
	@Excel(name = "所属专业", width = 15)
	@ApiModelProperty(value = "专业名称")
	@TableField(exist = false)
	private  String  majorName;
	/**子系统名称*/
	@Excel(name = "所属子系统", width = 15)
	@ApiModelProperty(value = "子系统名称")
	@TableField(exist = false)
	private  String  systemName;
	/**申领编号*/
	@Excel(name = "申领编号", width = 15)
	@ApiModelProperty(value = "申领编号")
	private String applyCode;
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
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
	@ApiModelProperty(value = "物资编号")
	private String materialCode;
	/**名称*/
	@Excel(name = "物资名称", width = 15)
	@ApiModelProperty(value = "物资名称")
	@TableField(exist = false)
	private  String  name;
	/**仓库编码*/
	@ApiModelProperty(value = "仓库编码")
	@Dict(dictTable ="spare_part_stock_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private String warehouseCode;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
	@ApiModelProperty(value = "仓库名称")
	@TableField(exist = false)
	private String warehouseName;
	/**入库数量*/
	@Excel(name = "入库数量", width = 15)
	@ApiModelProperty(value = "入库数量")
	private Integer num;
	/**组织机构id*/
	@ApiModelProperty(value = "组织机构id")
	private String orgId;
	/**确认时间*/
	@Excel(name = "确认时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "确认时间")
	private Date confirmTime;
	/**确认人ID*/
	@ApiModelProperty(value = "确认人ID")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	private String confirmId;
	/**确认人*/
	@Excel(name = "确认人", width = 15)
	@ApiModelProperty(value = "确认人")
	@TableField(exist = false)
	private String confirmName;
	/**二级库出库单号*/
	@ApiModelProperty(value = "二级库出库单号")
	private String outOrderCode;
	/**二级库出库人*/
	@ApiModelProperty(value = "二级库出库人")
	@TableField(exist = false)
	private String outOrderName;
	/**删除状态(0.未删除 1.已删除)*/
	@ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
	private Integer delFlag;
	/**创建人*/
	@ApiModelProperty(value = "创建人")
	@Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
	private String createBy;
	/**修改人*/
	@ApiModelProperty(value = "修改人")
	private String updateBy;
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
	/**ids*/
	@ApiModelProperty(value = "ids")
	@TableField(exist = false)
	private List<String> ids;
	/**批量入库*/
	@ApiModelProperty(value = "批量入库")
	@TableField(exist = false)
	private List<SparePartInOrder> orderList;
	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	@DeptFilterColumn
	private String sysOrgCode;
}
