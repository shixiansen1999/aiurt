package com.aiurt.boot.modules.appMessage.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description: MessagePageParam
 * @author: Mr.zhao
 * @date: 2021/11/11 19:50
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessagePageParam implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("页码")
	private Integer pageNo = 1;
	@ApiModelProperty("每页条数")
	private Integer pageSize = 10;

	@ApiModelProperty("获取新消息条数,默认5")
	private Integer listSize = 5;

	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty("开始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@ApiModelProperty("结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;


}
