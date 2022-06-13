package com.aiurt.boot.modules.patrol.param;


import com.aiurt.common.api.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: PoolPageParam
 * @author: Mr.zhao
 * @date: 2021/9/23 16:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel("请求参数")
public class PoolPageParam extends PageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "计划名称")
	private String patrolName;

	@ApiModelProperty(value = "所属系统")
	private String types;

	@ApiModelProperty(value="班组id")
	private String organizationId;

	@ApiModelProperty(value="站点id")
	private String stationId;

	@ApiModelProperty(value="线路id")
	private String lineId;

	@ApiModelProperty(value = "开始时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@ApiModelProperty(value = "结束时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	@ApiModelProperty(value="线路id集合")
	private List<Integer> stationIds;

	@ApiModelProperty(value = "权限班组集合(后台处理)")
	private List<String> departList;

	@ApiModelProperty(value = "权限系统集合(后台处理)")
	private List<String> systemCodes;

	@ApiModelProperty(value = "任务结束时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastTime;



}
