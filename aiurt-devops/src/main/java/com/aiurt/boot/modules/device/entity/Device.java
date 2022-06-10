package com.aiurt.boot.modules.device.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

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

	/**分类编号*/
	@Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
	private  String  typeCode;

	/**子系统编号*/
	@Excel(name = "子系统编号", width = 15)
    @ApiModelProperty(value = "子系统编号")
	private  String  systemCode;

	/**规格*/
	@Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
	private  String  specifications;

	/**线路编号*/
	@Excel(name = "线路编号", width = 15)
    @ApiModelProperty(value = "线路编号")
	private  String  lineCode;

	/**站点编号*/
	@Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
	private  String  stationCode;

	/**位置*/
	@Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
	private  String  location;

	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
	private  String  assetCode;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private  String  brand;

	/**出厂编号*/
	@Excel(name = "出厂编号", width = 15)
    @ApiModelProperty(value = "出厂编号")
	private  String  factoryCode;

	/**生产商*/
	@Excel(name = "生产商", width = 15)
    @ApiModelProperty(value = "生产商")
	private  String  manufacturer;

	/**供货厂商*/
	@Excel(name = "供货厂商", width = 15)
    @ApiModelProperty(value = "供货厂商")
	private  String  supplier;

	/**生产日期*/
	@Excel(name = "生产日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "生产日期")
	private  Date  productionDate;

	/**开始使用日期*/
	@Excel(name = "开始使用日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始使用日期")
	private  Date  startDate;

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

	/**删除状态 0-未删除 1-已删除*/
	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	private  Integer  delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间 CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间 CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 CURRENT_TIMESTAMP")
	private  Date  createTime;

	/**修改时间 根据当前时间戳更新*/
	@Excel(name = "修改时间 根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间 根据当前时间戳更新")
	private  Date  updateTime;

	/**价格*/
	@Excel(name = "价格", width = 15)
	@ApiModelProperty(value = "价格")
	private  Double  price;

	/**设备组件*/
	@Excel(name = "设备组件", width = 15)
	@ApiModelProperty(value = "设备组件")
	@TableField(exist = false)
	private List<DeviceAssembly> deviceAssembly;

	/**分类名*/
	@Excel(name = "分类名", width = 15)
	@ApiModelProperty(value = "分类名")
	@TableField(exist = false)
	private String typeName;

	/**系统名*/
	@Excel(name = "系统名", width = 15)
	@ApiModelProperty(value = "系统名")
	@TableField(exist = false)
	private String systemName;


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CODE = "code";
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


}
