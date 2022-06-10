package com.aiurt.boot.modules.apphome.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: HomeListParam
 * @author: Mr.zhao
 * @date: 2021/10/1 13:14
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "首页请求参数")
public class HomeListParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "查询状态 0.代办事项 1.已完成")
	@NotNull(message = "type不能为空")
	private Integer type;

	@ApiModelProperty(value = "查询状态 0&null.全部 1.巡检 2.检修 3.故障")
	private Integer taskType;

	@ApiModelProperty(value = "开始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@ApiModelProperty(value = "结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;


	@ApiModelProperty(value = "关键字")
	private String keyName;

	@ApiModelProperty(value = "userId,后台处理")
	private String userId;
}
