package com.aiurt.modules.device.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**所属专业*/
	@Excel(name = "所属专业", width = 15)
	@ApiModelProperty(value = "所属专业")
	@Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
	private  String  majorCode;

	/**子系统编号*/
	@Excel(name = "子系统编号", width = 15)
	@ApiModelProperty(value = "子系统编号")
	@Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
	private  String  systemCode;

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

	/**设备图纸*/
	@Excel(name = "设备图纸", width = 15)
	@ApiModelProperty(value = "设备图纸")
	private  String  picturePath;

	/**线路编号*/
	@Excel(name = "线路", width = 15)
	@ApiModelProperty(value = "线路编号")
	@Dict(dictTable ="cs_line",dicText = "line_name",dicCode = "line_code")
	private  String  lineCode;

	/**站点编号*/
	@Excel(name = "站点", width = 15)
	@ApiModelProperty(value = "站点编号")
	@Dict(dictTable ="cs_station",dicText = "station_name",dicCode = "station_code")
	private  String  stationCode;

	/**存放位置*/
	@Excel(name = "位置", width = 15)
	@ApiModelProperty(value = "位置")
	@Dict(dictTable ="cs_station_position",dicText = "position_name",dicCode = "position_code")
	private  String  positionCode;

	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
	@ApiModelProperty(value = "资产编号")
	private  String  assetCode;

	/**设备管理员*/
	@Excel(name = "设备管理员", width = 15)
	@ApiModelProperty(value = "设备管理员")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
	private  java.lang.String  manageUserName;

	/**负责班组*/
	@Excel(name = "负责班组", width = 15)
	@ApiModelProperty(value = "负责班组")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
	private  java.lang.String  orgCode;

	/**临时设备(是/否（默认否）1是,0:否)*/
//	@Excel(name = "临时设备(是/否（默认否）1是,0:否)", width = 15)
	@ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
	@Dict(dicCode = "device_temporary")
	private  java.lang.String  temporary;

	/**临时设备(是/否（默认否）1是,0:否)*/
	@Excel(name = "是否临时设备", width = 15)
	@ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
	private  java.lang.String  temporaryName;

	/**设备复用类型(1:多线路复用/0:多站点复用)*/
//	@Excel(name = "设备复用类型(1:多线路复用/0:多站点复用)", width = 15)
	@ApiModelProperty(value = "设备复用类型(1:多线路复用/0:多站点复用)")
	@Dict(dicCode = "device_reuse_type")
	private  java.lang.String  reuseType;

	@Excel(name = "设备复用类型", width = 15)
	@ApiModelProperty(value = "设备复用类型(1:多线路复用/0:多站点复用)")
	private  java.lang.String  reuseTypeName;

	/**出厂日期*/
	@Excel(name = "出厂日期", width = 15)
	@ApiModelProperty(value = "出厂日期")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private  java.lang.String  factoryDate;

	/**设备等级(字典值)*/
	@ApiModelProperty(value = "设备等级(字典值)")
	@Dict(dicCode = "device_level")
	private  java.lang.String  deviceLevel;

	/**设备等级(字典值)*/
	@Excel(name = "设备等级", width = 15)
	@ApiModelProperty(value = "设备等级")
	@TableField(exist = false)
	private  java.lang.String  deviceLevelName;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
	@ApiModelProperty(value = "品牌")
	private  java.lang.String  brand;

	/**供货厂商*/
	@Excel(name = "厂商", width = 15)
	@ApiModelProperty(value = "供货厂商")
	private  String  supplier;

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

	/**所属系统*/
//	@Excel(name = "所属系统", width = 15)
	@ApiModelProperty(value = "所属系统")
	@TableField(exist = false)
	private String systemName;

	/**所属线路*/
//	@Excel(name = "所属线路", width = 15)
	@ApiModelProperty(value = "所属线路")
	@TableField(exist = false)
	private String lineName;

	/**所属站点*/
//	@Excel(name = "所属站点", width = 15)
	@ApiModelProperty(value = "所属站点")
	@TableField(exist = false)
	private String stationName;

	/**删除状态 0-未删除 1-已删除*/
//	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
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
	private  String  scrapFlagName;

    private static final String ID = "id";
    private static final String NAME = "name";
    public static final String CODE = "code";
    private static final String TYPE_CODE = "type_code";
    private static final String SYSTEM_CODE = "system_code";
    private static final String SPECIFICATIONS = "specifications";
    private static final String LINE_CODE = "line_code";
    private static final String STATION_CODE = "station_code";
    private static final String LOCATION = "location";
    private static final String ASSET_CODE = "asset_code";
    private static final String BRAND = "brand";
    private static final String FACTORY_CODE = "factory_code";
    private static final String MANUFACTURER = "manufacturer";
    private static final String SUPPLIER = "supplier";
    private static final String PRODUCTION_DATE = "production_date";
    private static final String START_DATE = "start_date";
    private static final String SERVICE_LIFE = "service_life";
    private static final String TECHNICAL_PARAMETER = "technical_parameter";
    private static final String STATUS = "status";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";
	private static final String PRICE = "price";
	private static final String DEVICE_IP = "device_ip";


}
