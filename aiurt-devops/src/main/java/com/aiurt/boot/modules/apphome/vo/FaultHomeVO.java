package com.aiurt.boot.modules.apphome.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: FaultHomeVO
 * @author: Mr.zhao
 * @date: 2021/10/1 14:35
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FaultHomeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "标题")
	private String title;

	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;

	@ApiModelProperty(value = "类型 1.巡检 2.检修 3.故障 4.日志")
	private Integer type;

	@ApiModelProperty(value = "线路编号")
	private Integer lineCode;

	@ApiModelProperty(value = "状态字段: 0:未指派 1.未巡检 2.漏检")
	private Integer status;

	@ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
	private Integer faultLevel;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "故障发生时间")
	private Date occurrenceTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "维修完成时间")
	private Date overTime;


}
