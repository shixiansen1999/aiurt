package com.aiurt.boot.modules.apphome.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class WorkLogVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "标题")
	private String title;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date time;

	@ApiModelProperty(value = "类型 1.巡检 2.检修 3.故障 4.日志")
	private Integer type;

	@ApiModelProperty(value = "接班人")
	private String successName;


}
