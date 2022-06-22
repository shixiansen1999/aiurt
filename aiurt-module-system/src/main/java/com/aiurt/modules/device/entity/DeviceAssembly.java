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

import javax.validation.constraints.NotNull;

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
	@ApiModelProperty(value = "组件类型/物质类型")
	@Dict(dicCode = "device_base_type_code")
	private  String  baseTypeCode;

	/**组件类型/物质类型*/
	@Excel(name = "组件类型", width = 15)
	@ApiModelProperty(value = "组件类型/物质类型")
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

	/**设备编号*/
	@Excel(name = "所属设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
	private  String  deviceCode;


	/**组件名称*/
	@Excel(name = "组件名称", width = 15)
    @ApiModelProperty(value = "组件名称")
	@NotNull(message = "组件名称不能为空")
	private  String  materialName;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private  String  brand;

	/**组件类型*/
	@Excel(name = "组件类型", width = 15,replace ={"通用类_2","专用类_1"})
    @ApiModelProperty(value = "组件类型")
	private  Integer  type;

	/**供货商*/
	@Excel(name = "供货商", width = 15)
    @ApiModelProperty(value = "供货商")
	private  String  supplier;

	/**规格*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
	private  String  specifications;

	/**端口信息*/
	@Excel(name = "端口信息", width = 15)
	@ApiModelProperty(value = "端口信息")
	private  String  port;

	/**端口信息*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private  Integer amount;

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

	/**组件类型名称*/
//	@Excel(name = "组件类型名称", width = 15)
	@ApiModelProperty(value = "组件类型名称")
	@TableField(exist = false)
	private String assemblyName;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date startDate;

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
