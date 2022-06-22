package com.aiurt.modules.device.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;


/**
 * @Description: 设备组件
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("device_assembly")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="device_assembly对象", description="设备组件")
public class DeviceAssembly {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**组件类型/物质类型*/
	@ApiModelProperty(value = "物资类型编码")
	@Dict(dictTable ="material_base_type",dicText = "base_type_name",dicCode = "base_type_code")
	private  String  baseTypeCode;
	/**组件类型/物质类型*/
	@Excel(name = "物资类型", width = 15)
	@ApiModelProperty(value = "物资类型")
	@TableField(exist = false)
	private  String  baseTypeCodeName;

	/**组件状态*/
	@ApiModelProperty(value = "组件状态")
	@Dict(dicCode = "device_assembly_status")
	private  String  status;
	/**组件状态*/
	@Excel(name = "组件状态", width = 15)
	@ApiModelProperty(value = "组件状态")
	private  String  statusName;

	/**组件编号*/
	@Excel(name = "组件编号", width = 15)
	@ApiModelProperty(value = "组件编号")
	private  String  code;

	/**生产厂商（厂商信息）*/
	@Excel(name = "生产厂商（厂商信息）", width = 15)
	@ApiModelProperty(value = "生产厂商（厂商信息）")
	private  String  manufactorCode;

	/**设备编号*/
	@Excel(name = "所属设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private  String  deviceCode;

	/**组件名称*/
	@Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
	private  String  materialName;

	/**物资基础数据编号-物资编号*/
	@Excel(name = "物资基础数据编号-物资编号", width = 15)
	@ApiModelProperty(value = "物资基础数据编号-物资编号")
	private  String  materialCode;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  remark;

	/**规格*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
	private  String  specifications;

	/**删除状态 0-未删除 1-已删除*/
//	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	/**创建人*/
//	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
//	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间*/
//	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  java.util.Date  createTime;

	/**修改时间*/
//	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  java.util.Date  updateTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date startDate;

	/**附件*/
	@Excel(name = "附件", width = 15)
	@ApiModelProperty(value = "附件")
	private  String  path;

	/**价格（元）*/
	@Excel(name = "价格（元）", width = 15)
	@ApiModelProperty(value = "价格（元）")
	private  String  price;

	/**设备类型编码*/
	@Excel(name = "设备类型编码", width = 15)
	@ApiModelProperty(value = "设备类型编码")
	private  String  deviceTypeCode;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "购买日期")
	private java.util.Date buyDate;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "上线")
	private java.util.Date onlineDate;

	/**单位*/
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	private  String  unit;

}
