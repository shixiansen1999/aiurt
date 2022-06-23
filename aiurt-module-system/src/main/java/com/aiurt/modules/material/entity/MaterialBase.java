package com.aiurt.modules.material.entity;

import com.aiurt.common.aspect.annotation.Dict;
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
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("material_base")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="物资", description="物资")
public class MaterialBase {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  Long  id;

	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
	@ApiModelProperty(value = "专业编码")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	private  String  majorCode;
	/**所属专业手动翻译*/
	@Excel(name = "所属专业手动翻译", width = 15)
	@ApiModelProperty(value = "所属专业手动翻译")
	@TableField(exist = false)
	private  String  majorCodeName;

	/**子系统编号*/
	@Excel(name = "子系统编号", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	private  String  systemCode;
	/**子系统编号手动翻译*/
	@Excel(name = "子系统编号手动翻译", width = 15)
	@ApiModelProperty(value = "子系统编号手动翻译")
	@TableField(exist = false)
	private  String  systemCodeName;

	/**分类编码*/
	@Excel(name = "分类编码", width = 15)
	@ApiModelProperty(value = "分类编码")
	@Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "base_type_code")
	private  String  baseTypeCode;
	/**分类编码*/
	@Excel(name = "分类编码名称", width = 15)
	@ApiModelProperty(value = "分类编码名称")
	@TableField(exist = false)
	private  String  baseTypeCodeName;

	/**分类编码层级*/
	@Excel(name = "分类编码层级", width = 15)
	@ApiModelProperty(value = "分类编码层级")
	private  String  baseTypeCodeCc;
	/**分类编码层级名称*/
	@Excel(name = "分类编码层级名称", width = 15)
	@ApiModelProperty(value = "分类编码层级名称")
	@TableField(exist = false)
	private  String  baseTypeCodeCcName;

	/**编码*/
	@Excel(name = "编码", width = 15)
    @ApiModelProperty(value = "编码")
	private  String  code;

	/**名称*/
	@Excel(name = "名称", width = 15)
	@ApiModelProperty(value = "名称")
	private  String  name;

	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
	@ApiModelProperty(value = "规格型号")
	private  String  specifications;

	/**生产厂商*/
	@Excel(name = " 生产厂商", width = 15)
	@ApiModelProperty(value = " 生产厂商")
	@Dict(dictTable ="cs_manufactor",dicText = "name",dicCode = "code")
	private  String  manufactorCode;
	/**生产厂商手动翻译*/
	@Excel(name = "生产厂商手动翻译", width = 15)
	@ApiModelProperty(value = "生产厂商手动翻译")
	@TableField(exist = false)
	private  String  manufactorCodeName;

	/**单位*/
	@Excel(name = " 单位", width = 15)
	@ApiModelProperty(value = " 单位")
	private  String  unit;

	/**单价*/
	@Excel(name = " 单价", width = 15)
	@ApiModelProperty(value = " 单价")
	private  String  price;

	/**所属部门编码*/
	@Excel(name = "所属部门编码", width = 15)
	@ApiModelProperty(value = "所属部门编码")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
	private  String  sysOrgCode;
	/**所属部门名称*/
	@Excel(name = "所属部门名称", width = 15)
	@ApiModelProperty(value = "所属部门名称")
	@TableField(exist = false)
	private  String  sysOrgCodeName;

	/**创建人*/
//	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
//	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间 CURRENT_TIMESTAMP*/
//	@Excel(name = "创建时间 CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 CURRENT_TIMESTAMP")
	private  java.util.Date  createTime;

	/**修改时间 根据当前时间戳更新*/
//	@Excel(name = "修改时间 根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间 根据当前时间戳更新")
	private  java.util.Date  updateTime;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	@Dict(dicCode = "material_base_type_del_flag")
	private  Integer  delFlag;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableField(exist = false)
	private  String  deviceCode;
}
