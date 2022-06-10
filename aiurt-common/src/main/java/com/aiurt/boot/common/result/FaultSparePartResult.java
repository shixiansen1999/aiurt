package com.aiurt.boot.common.result;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
public class FaultSparePartResult {

	@Excel(name="序号",width = 15)
	@TableField(exist = false)
	private Integer serialNumber;

	/**故障时间*/
	@Excel(name = "故障时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "故障发生时间")
	private Date occurrenceTime;

	/**线路*/
	@Excel(name = "线路", width = 15)
	@ApiModelProperty(value = "线路")
	private String lineName;

	/**站点*/
	@Excel(name = "站点", width = 15)
	@ApiModelProperty(value = "站点")
	private String stationName;

	/**故障设备编号集合*/
	@ApiModelProperty(value = "设备")
	private String device;

	/**故障设备*/
	@Excel(name = "设备", width = 20)
	@ApiModelProperty(value = "设备")
	private String deviceName;

	/**故障类型*/
	@ApiModelProperty(value = "故障类型")
	private Integer faultType;

	/**故障类型*/
	@Excel(name = "故障类型", width = 15)
	@ApiModelProperty(value = "故障类型")
	private String faultTypeDesc;

	/**故障现象*/
	@Excel(name = "详细描述", width = 15)
	@ApiModelProperty(value = "详细描述")
	private String faultPhenomenon;

	/**故障时间*/
	@Excel(name = "申报时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "故障发生时间")
	private Date createTime;

	/**创建人*/
	@Excel(name = "申报人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

}
