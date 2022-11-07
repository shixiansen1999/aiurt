package com.aiurt.modules.stock.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 出库物资表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_outbound_materials")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="出库物资表", description="出库物资表")
public class StockOutboundMaterials extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**出库单号*/
	@Excel(name = "出库单号")
	@ApiModelProperty(value = "出库单号")
	private  String  outOrderCode;

	/**物资编号*/
	@Excel(name = "物资编号")
	@ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**出库仓库编号*/
	@Excel(name = "出库仓库编号")
	@ApiModelProperty(value = "出库仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;

	/**备注*/
	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private  String  remark;

	/**仓库现有库存*/
	@Excel(name = "仓库现有库存")
	@ApiModelProperty(value = "仓库现有库存")
	private  Integer  inventory;

	/**申请出库数量*/
	@Excel(name = "申请出库数量")
	@ApiModelProperty(value = "申请出库数量")
	private  Integer  applyOutput;

	/**出库数量*/
	@Excel(name = "实际出库数量")
	@ApiModelProperty(value = "实际出库数量")
	private  Integer  actualOutput;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
	private  String  createBy;

	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 CURRENT_TIMESTAMP")
	private  java.util.Date  createTime;

	/**修改时间 根据当前时间戳更新*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间 根据当前时间戳更新")
	private  java.util.Date  updateTime;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	@Excel(name = "物资分类")
	@ApiModelProperty(value = "物资分类")
	@TableField(exist = false)
	private  String  baseTypeCodeCc;
	@Excel(name = "物资分类分级翻译")
	@ApiModelProperty(value = "物资分类分级翻译")
	@TableField(exist = false)
	private  String  baseTypeCodeCcName;

	/**专业编码*/
	@ApiModelProperty(value = "专业编码")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	@TableField(exist = false)
	private  String  majorCode;
	@ApiModelProperty(value = "专业名称")
	@TableField(exist = false)
	private  String  majorCodeName;

	/**子系统编号*/
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	@TableField(exist = false)
	private  String  systemCode;
	@ApiModelProperty(value = "子系统名称")
	@TableField(exist = false)
	private  String  systemCodeName;

	/**类型*/
	@ApiModelProperty(value = "类型")
	@Dict(dicCode = "material_type")
	@TableField(exist = false)
	private  String  type;
	@ApiModelProperty(value = "类型名称")
	@TableField(exist = false)
	private  String  typeName;

	/**名称*/
	@ApiModelProperty(value = "物资名称")
	@TableField(exist = false)
	private  String  name;

	/**单位*/
	@ApiModelProperty(value = " 单位")
	@Dict(dicCode = "materian_unit")
	@TableField(exist = false)
	private  String  unit;
	@ApiModelProperty(value = "单位名称")
	@TableField(exist = false)
	private  String  unitName;

	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
	@ApiModelProperty(value = "规格型号")
	@TableField(exist = false)
	private  String  specifications;

	/**生产厂商*/
	@Excel(name = "生产厂商编码", width = 15)
	@ApiModelProperty(value = "生产厂商编码")
	@TableField(exist = false)
	@Dict(dictTable ="cs_manufactor",dicText = "name",dicCode = "id")
	private  String  manufactorCode;
	/**生产厂商手动翻译*/
	@Excel(name = "生产厂商名称", width = 15)
	@ApiModelProperty(value = "生产厂商名称")
	@TableField(exist = false)
	private  String  manufactorCodeName;

	/**单价*/
	@Excel(name = "单价", width = 15)
	@ApiModelProperty(value = " 单价")
	@TableField(exist = false)
	private  String  price;

	@Excel(name = "是否是易耗品：0否 1是", width = 15)
	@ApiModelProperty(value = "是否是易耗品：0否 1是 默认为0")
	@Dict(dicCode = "consumables_type")
	@TableField(exist = false)
	private java.lang.Integer consumablesType = 0;
}
