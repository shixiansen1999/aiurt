package com.aiurt.boot.modules.patrol.param;


import com.aiurt.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description: PatrolPoolParam
 * @author: Mr.zhao
 * @date: 2021/9/16 23:18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolPoolParam extends PageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检表名称")
	private String patrolName;

	@ApiModelProperty(value = "班组id")
	private String organizationId;

	@ApiModelProperty(value = "线路id")
	private String lineId;

	@ApiModelProperty(value = "巡检人")
	private String name;

	@ApiModelProperty(value = "所属系统名称")
	private String systemTypeName;

	@ApiModelProperty(value = "是否手动下发任务: 0.否 1.是")
	private Integer type;

	@ApiModelProperty(value = "是否完成状态 0.否 1.是")
	private Integer submitStatus;

	@ApiModelProperty(value = "完成开始时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty(value = "完成结束时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	@ApiModelProperty(value = "巡检人id,后端处理")
	private String id;

	@ApiModelProperty(value = "是否为漏检任务: 0.否 1.是")
	private Integer ignoreStatus;

	@ApiModelProperty(value = "是否处理: 0 未处理 1 已处理")
	private Integer ignoreTimeStatus;

	@ApiModelProperty(value = "漏检开始时间")
	private String ignoreStartTime;

	@ApiModelProperty(value = "漏检结束时间")
	private String ignoreEndTime;

	@ApiModelProperty(value = "站点id")
	private String stationId;

	@ApiModelProperty(value = "完成开始时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createStartTime;

	@ApiModelProperty(value = "完成结束时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createEndTime;

	@ApiModelProperty(value="线路id集合")
	private List<Integer> stationIds;


	@ApiModelProperty(value = "权限班组集合(后台处理)")
	private List<String> departList;

	@ApiModelProperty(value = "权限系统集合(后台处理)")
	private List<String> systemCodes;

}
