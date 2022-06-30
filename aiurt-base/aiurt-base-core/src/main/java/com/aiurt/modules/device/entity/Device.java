package com.aiurt.modules.device.entity;

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

import java.util.List;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("device")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="device对象", description="设备(system)")
public class Device {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**所属专业*/
	@Excel(name = "所属专业", width = 15)
	@ApiModelProperty(value = "所属专业")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	private  String  majorCode;
	/**所属专业名称*/
	@Excel(name = "所属专业名称", width = 15)
	@ApiModelProperty(value = "所属专业名称")
	@TableField(exist = false)
	private  String  majorCodeName;

	/**子系统编号*/
	@Excel(name = "子系统编号", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	private  String  systemCode;
	/**子系统编号名称*/
	@Excel(name = "子系统编号名称", width = 15)
	@ApiModelProperty(value = "子系统编号名称")
	@TableField(exist = false)
	private  String  systemCodeName;

	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
	@ApiModelProperty(value = "设备名称")
	private  String  name;

	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private  String  code;

	/**设备类型编码*/
	@Excel(name = "设备类型编码", width = 15)
	@ApiModelProperty(value = "设备类型编码")
	@Dict(dictTable ="device_Type",dicText = "name",dicCode = "code")
	private  String  deviceTypeCode;
	/**设备类型编码*/
	@Excel(name = "设备类型编码名称", width = 15)
	@ApiModelProperty(value = "设备类型编码名称")
	@TableField(exist = false)
	private  String  deviceTypeCodeName;

	/**设备类型编码层级*/
	@Excel(name = "设备类型编码层级", width = 15)
	@ApiModelProperty(value = "设备类型编码层级")
	@Dict(dictTable ="device_Type",dicText = "name",dicCode = "code")
	private  String  deviceTypeCodeCc;
	/**设备类型编码名称层级*/
	@Excel(name = "设备类型编码名称层级", width = 15)
	@ApiModelProperty(value = "设备类型编码名称层级")
	@TableField(exist = false)
	private  String  deviceTypeCodeCcName;

	/**设备图纸*/
	@Excel(name = "设备图纸", width = 15)
	@ApiModelProperty(value = "设备图纸")
	private  String  picturePath;

	/**线路编号*/
	@Excel(name = "线路", width = 15)
	@ApiModelProperty(value = "线路编号")
	@Dict(dictTable ="cs_line",dicText = "line_name",dicCode = "line_code")
	private  String  lineCode;
	/**线路名称*/
	@Excel(name = "线路名称", width = 15)
	@ApiModelProperty(value = "线路名称")
	@TableField(exist = false)
	private  String  lineCodeName;

	/**站点编号*/
	@Excel(name = "站点", width = 15)
	@ApiModelProperty(value = "站点编号")
	@Dict(dictTable ="cs_station",dicText = "station_name",dicCode = "station_code")
	private  String  stationCode;
	/**站点名称*/
	@Excel(name = "站点名称", width = 15)
	@ApiModelProperty(value = "站点名称")
	@TableField(exist = false)
	private  String  stationCodeName;

	/**存放位置*/
	@Excel(name = "位置", width = 15)
	@ApiModelProperty(value = "位置")
	@Dict(dictTable ="cs_station_position",dicText = "position_name",dicCode = "position_code")
	private  String  positionCode;
	/**位置名称*/
	@Excel(name = "位置名称", width = 15)
	@ApiModelProperty(value = "位置名称")
	@TableField(exist = false)
	private  String  positionCodeName;

	@Excel(name = "位置层级", width = 15)
	@ApiModelProperty(value = "位置层级")
	@TableField(exist = false)
	private  String  positionCodeCc;
	@Excel(name = "位置层级名称", width = 15)
	@ApiModelProperty(value = "位置层级名称")
	@TableField(exist = false)
	private  String  positionCodeCcName;

	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
	@ApiModelProperty(value = "资产编号")
	private  String  assetCode;

	/**设备管理员*/
	@Excel(name = "设备管理员", width = 15)
	@ApiModelProperty(value = "设备管理员")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
	private  String  manageUserName;
	/**设备管理员名称*/
	@Excel(name = "设备管理员名称", width = 15)
	@ApiModelProperty(value = "设备管理员名称")
	@TableField(exist = false)
	private  String  manageUserNameName;

	/**负责班组*/
	@Excel(name = "负责班组", width = 15)
	@ApiModelProperty(value = "负责班组")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
	private  String  orgCode;
	/**负责班组名称*/
	@Excel(name = "负责班组名称", width = 15)
	@ApiModelProperty(value = "负责班组名称")
	@TableField(exist = false)
	private  String  orgCodeName;

	/**临时设备(是/否（默认否）1是,0:否)*/
//	@Excel(name = "临时设备(是/否（默认否）1是,0:否)", width = 15)
	@ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
	@Dict(dicCode = "device_temporary")
	private  String  temporary;
	/**临时设备(是/否（默认否）1是,0:否)*/
	@Excel(name = "是否临时设备", width = 15)
	@ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
	@TableField(exist = false)
	private  String  temporaryName;

	/**设备复用类型(1:多线路复用/0:多站点复用)*/
//	@Excel(name = "设备复用类型(1:多线路复用/0:多站点复用)", width = 15)
	@ApiModelProperty(value = "设备复用类型(1:多线路复用/0:多站点复用)")
//	@Dict(dicCode = "device_reuse_type")
	private  String  reuseType;
	@Excel(name = "设备复用类型", width = 15)
	@ApiModelProperty(value = "设备复用类型(1:多线路复用/0:多站点复用)")
	@TableField(exist = false)
	private  String  reuseTypeName;

	/**出厂日期*/
	@Excel(name = "出厂日期", width = 15)
	@ApiModelProperty(value = "出厂日期")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private  String  factoryDate;

	/**设备等级(字典值)*/
	@ApiModelProperty(value = "设备等级(字典值)")
	@Dict(dicCode = "device_level")
	private  String  deviceLevel;
	/**设备等级(字典值)*/
	@Excel(name = "设备等级", width = 15)
	@ApiModelProperty(value = "设备等级")
	@TableField(exist = false)
	private  String  deviceLevelName;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
	@ApiModelProperty(value = "品牌")
	private  String  brand;

	/**供应商(厂商信息表_编码)*/
	@Excel(name = "供应商(厂商信息表_编码)", width = 15)
	@ApiModelProperty(value = "供应商(厂商信息表_编码)")
	@Dict(dictTable ="cs_manufactor",dicText = "name",dicCode = "code")
	private  String  manufactorCode;
	/**生产厂商手动翻译*/
	@Excel(name = "生产厂商手动翻译", width = 15)
	@ApiModelProperty(value = "生产厂商手动翻译")
	@TableField(exist = false)
	private  String  manufactorCodeName;

	/**设备sn*/
	@Excel(name = "设备SN", width = 15)
	@ApiModelProperty(value = "设备sn")
	@TableField(value = "device_sn")
	private  String  deviceSn;

	/**设备ip*/
	@Excel(name = "设备IP", width = 15)
	@ApiModelProperty(value = "设备ip")
	private  String  deviceIp;

	/**出厂编号*/
	@Excel(name = "出厂编号", width = 15)
	@ApiModelProperty(value = "出厂编号")
	private  String  factoryCode;

	/**生产日期*/
	@Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "生产日期")
	private  java.util.Date  productionDate;

	/**开始使用日期*/
	@Excel(name = "开始使用日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "开始使用日期")
	private  java.util.Date  startDate;

	/**使用年限*/
	@Excel(name = "使用年限", width = 15)
	@ApiModelProperty(value = "使用年限")
	private  Integer  serviceLife;

	/**技术参数*/
	@Excel(name = "技术参数", width = 15)
	@ApiModelProperty(value = "技术参数")
	private  String  technicalParameter;

	/**状态 0-停用 1-正常*/
//	@Excel(name = "状态 0-停用 1-正常", width = 15)
	@ApiModelProperty(value = "状态 0-停用 1-正常")
	@Dict(dicCode = "device_status")
	private  Integer  status;

	/**价格*/
	@Excel(name = "价格", width = 15)
	@ApiModelProperty(value = "价格")
	private  Double  price;

	/**状态 0-停用 1-正常*/
	@Excel(name = "状态", width = 15)
	@ApiModelProperty(value = "状态 0-停用 1-正常")
	@TableField(exist = false)
	private  String  statusDesc;

	/**设备分类*/
//	@Excel(name = "设备分类", width = 15)
	@ApiModelProperty(value = "设备分类")
	@TableField(exist = false)
	private String typeName;

	/**删除状态 0-未删除 1-已删除*/
//	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableField(exist = false)
	private  String  delFlagName;

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

	@ApiModelProperty(value = "设备组件")
	@TableField(exist = false)
	private List<DeviceAssembly> deviceAssemblyList;

	/**报废时间 CURRENT_TIMESTAMP*/
	//	@Excel(name = "创建时间 CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "报废时间 CURRENT_TIMESTAMP")
	private  java.util.Date  scrapTime;

	/**报废状态 0-未报废 1-已报废*/
	//	@Excel(name = "报废状态 0-未报废 1-已报废", width = 15)
	@ApiModelProperty(value = "报废状态 0-未报废 1-已报废")
	@Dict(dicCode = "device_scrap_flag")
	private  Integer  scrapFlag;

	@Excel(name = "报废状态")
	@ApiModelProperty(value = "报废状态 0-未报废 1-已报废")
	@TableField(exist = false)
	private  String  scrapFlagName;
	@Excel(name = "巡检标准Code")
	@ApiModelProperty(value = "巡检标准Code")
	@TableField(exist = false)
	private  String  planStandardCode;

}
