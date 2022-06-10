package com.aiurt.boot.modules.patrol.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 统计请求参数
 *
 * @description: StatisticsParam
 * @author: Mr.zhao
 * @date: 2021/11/19 9:28
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class StatisticsParam implements Serializable {

	@ApiModelProperty(value = "站点")
	private String stationId;

	@ApiModelProperty(value = "线路")
	private String lineId;

	@ApiModelProperty(value = "班组id")
	private String organizationId;

	@ApiModelProperty(value = "开始时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@ApiModelProperty(value = "结束时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	@ApiModelProperty(value = "权限站点集合(后台处理)")
	private List<Integer> stationIds;

	@ApiModelProperty(value = "权限班组集合(后台处理)")
	private List<String> departList;

	@ApiModelProperty(value = "权限系统集合(后台处理)")
	private List<String> systemCodes;

}
