package com.aiurt.boot.modules.device.entity;

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
@ApiModel(value="device对象", description="设备")
public class Device {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
	@ApiModelProperty(value = "设备名称")
	private  String  name;

	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private  String  code;

	/**子系统编号*/
	@Excel(name = "所属系统", width = 15)
	@ApiModelProperty(value = "子系统编号")
	private  String  systemCode;

	/**分类编号*/
	@Excel(name = "设备大类", width = 15)
	@ApiModelProperty(value = "分类编号")
	private  String  typeCode;

	@ApiModelProperty(value = "设备小类")
	private  String  smallTypeCode;

	@ApiModelProperty(value = "设备小类")
	@Excel(name = "设备小类", width = 15)
	@TableField(exist = false)
	private  String  smallTypeName;

	/**型号规格*/
	@Excel(name = "规格", width = 15)
	@ApiModelProperty(value = "规格")
	private  String  specifications;

	/**线路编号*/
	@Excel(name = "线路", width = 15)
	@ApiModelProperty(value = "线路编号")
	private  String  lineCode;

	/**站点编号*/
	@Excel(name = "站点", width = 15)
	@ApiModelProperty(value = "站点编号")
	private  String  stationCode;

	/**存放位置*/
	@Excel(name = "位置", width = 15)
	@ApiModelProperty(value = "位置")
	private  String  location;

	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
	@ApiModelProperty(value = "资产编号")
	private  String  assetCode;

	/**品牌*/
//	@Excel(name = "品牌", width = 15)
//	@ApiModelProperty(value = "品牌")
//	private  java.lang.String  brand;

	/**生产商*/
//	@Excel(name = "生产商", width = 15)
//	@ApiModelProperty(value = "生产商")
//	private  java.lang.String  manufacturer;

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
	@Excel(name = "状态 0-停用 1-正常", width = 15)
	@ApiModelProperty(value = "状态 0-停用 1-正常")
	private  Integer  status;

	/**价格*/
	@Excel(name = "价格", width = 15)
	@ApiModelProperty(value = "价格")
	private  Double  price;

	/**物资组件*/
//	@Excel(name = "设备组件", width = 15)
	@ApiModelProperty(value = "物资组件")
	private  String  materials;

	/**状态 0-停用 1-正常*/
//	@Excel(name = "状态", width = 15)
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
	private  Integer  scrapFlag;

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
