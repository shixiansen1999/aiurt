package com.aiurt.modules.material.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
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

import java.util.List;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("material_base_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="物资分类", description="物资分类")
public class MaterialBaseType {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**所属专业*/
	@Excel(name = "专业编码", width = 15)
	@ApiModelProperty(value = "专业编码")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	@MajorFilterColumn
	private  String  majorCode;
	@ApiModelProperty(value = "专业名称")
	@TableField(exist = false)
	private  String  majorName;

	/**子系统编号*/
	@Excel(name = "子系统编号", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	@SystemFilterColumn
	private  String  systemCode;
	@ApiModelProperty(value = "子系统名称")
	@TableField(exist = false)
	private  String  systemName;

	/**分类编码*/
	@Excel(name = "分类编码", width = 15)
	@ApiModelProperty(value = "分类编码")
	private  String  baseTypeCode;

	/**分类名称*/
	@Excel(name = "分类名称", width = 15)
    @ApiModelProperty(value = "分类名称")
	private  String  baseTypeName;

	/**设备类型编码*/
	@Excel(name = "分类状态", width = 15)
	@ApiModelProperty(value = "分类状态,默认启用，1：启用，0停用")
	@Dict(dicCode ="material_base_type_status")
	private  String  status;

	/**父id*/
	@Excel(name = "父id", width = 15)
	@ApiModelProperty(value = "父id")
	@Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "id")
	private  String  pid;
	/**父分类名称*/
	@Excel(name = "父id", width = 15)
	@ApiModelProperty(value = "父id")
	@TableField(exist = false)
	private  String  pidName;

	/**线路编号*/
	@Excel(name = " 分类编码层级", width = 15)
	@ApiModelProperty(value = " 分类编码层级")
	private  String  typeCodeCc;

	/**站点编号*/
	@Excel(name = "所属部门编码", width = 15)
	@ApiModelProperty(value = "所属部门编码")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
	@DeptFilterColumn
	private  String  sysOrgCode;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
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

	/**子集*/
	@Excel(name = "子集", width = 15)
	@ApiModelProperty(value = "子集")
	@TableField(exist = false)
	private  List<MaterialBaseType>  materialBaseTypeList;

	@ApiModelProperty(value = "备用字段")
	@TableField(exist = false)
	private String byType = "wzfl";

	@ApiModelProperty(value = "父级状态")
	@TableField(exist = false)
	private String pStatus ;
}
