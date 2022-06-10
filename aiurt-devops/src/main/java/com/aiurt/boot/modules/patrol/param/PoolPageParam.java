package com.aiurt.boot.modules.patrol.param;

import com.swsc.copsms.common.api.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
	private String name;

	@ApiModelProperty(value = "所属系统")
	private Integer systemType;

	@ApiModelProperty(value="班组id")
	private String organizationId;

	@ApiModelProperty(value = "开始时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	@ApiModelProperty(value = "结束时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;

}
