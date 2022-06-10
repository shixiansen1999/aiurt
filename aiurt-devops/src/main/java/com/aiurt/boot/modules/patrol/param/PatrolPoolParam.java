package com.aiurt.boot.modules.patrol.param;

import com.swsc.copsms.common.api.vo.PageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
	private Long organizationId;

	@ApiModelProperty(value = "巡检人")
	private String name;

	@ApiModelProperty(value = "是否手动下发任务: 0.否 1.是")
	private Integer type;

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




}
