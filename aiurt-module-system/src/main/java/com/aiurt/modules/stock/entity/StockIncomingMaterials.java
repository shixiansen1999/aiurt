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
 * @Description: 入库物资表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_incoming_materials")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="入库物资表", description="入库物资表")
public class StockIncomingMaterials extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**入库单号*/
	@Excel(name = "入库单号")
	@ApiModelProperty(value = "入库单号")
	private  String  inOrderCode;

	/**物资编号*/
	@Excel(name = "物资编号")
	@ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**入库数量*/
	@Excel(name = "入库数量")
	@ApiModelProperty(value = "入库数量")
	private  Integer  number;

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

	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
	@ApiModelProperty(value = "专业编码")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	@TableField(exist = false)
	private  String  majorCode;

	/**子系统编号*/
	@Excel(name = "子系统编码", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	@TableField(exist = false)
	private  String  systemCode;

	/**分类编码层级*/
	@ApiModelProperty(value = "分类编码层级")
	@TableField(exist = false)
	private  String  baseTypeCodeCc;
	/**分类编码层级*/
	@ApiModelProperty(value = "分类编码层级")
	@TableField(exist = false)
	@Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "base_type_code")
	private  String  baseTypeCode;
	/**分类编码层级名称*/
	@ApiModelProperty(value = "分类编码层级名称")
	@TableField(exist = false)
	private  String  baseTypeCodeCcName;

	/**类型*/
	@Excel(name = "类型", width = 15)
	@ApiModelProperty(value = "类型")
	@Dict(dicCode = "material_type")
	@TableField(exist = false)
	private  String  type;

	/**名称*/
	@Excel(name = "物资名称", width = 15)
	@ApiModelProperty(value = "物资名称")
	@TableField(exist = false)
	private  String  name;

	/**单位*/
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = " 单位")
	@Dict(dicCode = "materian_unit")
	@TableField(exist = false)
	private  String  unit;
}
