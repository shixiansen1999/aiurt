package com.aiurt.boot.modules.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 设备小类
 * @Author: swsc
 * @Date:   2021-12-29
 * @Version: V1.0
 */
@Data
@TableName("device_small_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="device_small_type对象", description="设备小类")
public class DeviceSmallType {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private Integer id;
	/**deviceTypeId*/
	@Excel(name = "deviceTypeId", width = 15)
    @ApiModelProperty(value = "设备类型id")
	private Integer deviceTypeId;
	/**name*/
	@Excel(name = "name", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**code*/
	@Excel(name = "code", width = 15)
    @ApiModelProperty(value = "数据值")
	private String code;
	/**describe*/
	@Excel(name = "describes", width = 15)
    @ApiModelProperty(value = "描述")
	private String describes;
	/**sort*/
	@Excel(name = "sort", width = 15)
    @ApiModelProperty(value = "排序值")
	private Integer sort;
	/**status*/
	@Excel(name = "status", width = 15)
    @ApiModelProperty(value = "状态 0-启用，1-停用")
	private Integer status;
	/**delFlag*/
	@Excel(name = "delFlag", width = 15)
    @ApiModelProperty(value = "删除标识")
	private Integer delFlag;

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
	private Date createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private  Date  updateTime;

	public static final String CODE = "code";
	public static final String NAME = "name";
}
