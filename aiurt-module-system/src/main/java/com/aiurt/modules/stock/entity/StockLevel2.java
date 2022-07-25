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
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_level2")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="二级库", description="二级库")
public class StockLevel2 extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**物资编号*/
	@Excel(name = "物资编号")
	@ApiModelProperty(value = "物资编号")
	@Dict(dictTable ="material_base",dicText = "name",dicCode = "code")
	private  String  materialCode;

	/**物资名称*/
	@ApiModelProperty(value = "物资名称")
	@TableField(exist = false)
	private  String  materialName;

	/**物资分类编码*/
	@Excel(name = "物资分类编码")
	@ApiModelProperty(value = "物资分类编码")
	private  String  baseTypeCode;

	/**物资分类编码分级手动翻译*/
	@ApiModelProperty(value = "物资分类编码分级手动翻译")
	@TableField(exist = false)
	private  String  baseTypeCodeName;

	/**存放仓库编号*/
	@Excel(name = "存放仓库编号")
	@ApiModelProperty(value = "存放仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;

	/**数量*/
	@Excel(name = "数量")
    @ApiModelProperty(value = "数量")
	private  Integer  num;

	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
	@ApiModelProperty(value = "专业编码")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	private  String  majorCode;

	/**子系统编号*/
	@Excel(name = "子系统编码", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	private  String  systemCode;

	/**入库时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "入库时间")
	private  java.util.Date  stockInTime;

	/**备注*/
	@Excel(name = "备注", width = 15)
	@ApiModelProperty(value = "备注")
	private  String  remark;

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

	/**支持专业、子系统、物资三种一起模糊查询的物资分类条件*/
	@ApiModelProperty(value = "支持专业、子系统、物资三种一起模糊查询的物资分类条件")
	@TableField(exist = false)
	private  String  selectMaterialType;

	/**物资类型*/
	@ApiModelProperty(value = "物资类型")
	@TableField(exist = false)
	@Dict(dicCode = "material_type")
	private  String  type;

	/**组织机构id*/
	@Excel(name = "组织机构id")
	@ApiModelProperty(value = "组织机构id")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	@TableField(exist = false)
	private  String  organizationId;

	/**单位*/
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = " 单位")
	@Dict(dicCode = "materian_unit")
	@TableField(exist = false)
	private  String  unit;

	/**分类无层级*/
	@ApiModelProperty(value = " 分类无层级")
	@TableField(exist = false)
	private  String  baseType;
}
