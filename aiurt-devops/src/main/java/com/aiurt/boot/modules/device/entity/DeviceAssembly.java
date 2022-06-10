package com.aiurt.boot.modules.device.entity;

import java.io.Serializable;
import java.util.Date;
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

	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private  String  deviceCode;

	/**组件名称*/
	@Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
	private  String  name;

	/**组件编号*/
	@Excel(name = "组件编号", width = 15)
    @ApiModelProperty(value = "组件编号")
	private  String  code;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private  String  brand;

	/**组件类型*/
	@Excel(name = "组件类型", width = 15)
    @ApiModelProperty(value = "组件类型")
	private  Integer  type;

	/**供货商*/
	@Excel(name = "供货商", width = 15)
    @ApiModelProperty(value = "供货商")
	private  String  supplier;

	/**规格*/
	@Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
	private  String  specifications;

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

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  Date  updateTime;

	/**组件类型*/
	@Excel(name = "组件类型", width = 15)
	@ApiModelProperty(value = "组件类型")
	@TableField(exist = false)
	private String assemblyName;


    private static final String ID = "id";
    private static final String DEVICE_CODE = "device_code";
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String BRAND = "brand";
    private static final String TYPE = "type";
    private static final String SUPPLIER = "supplier";
    private static final String SPECIFICATIONS = "specifications";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
