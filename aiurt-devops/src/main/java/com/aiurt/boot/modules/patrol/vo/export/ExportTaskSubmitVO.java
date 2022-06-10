package com.aiurt.boot.modules.patrol.vo.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Mr.zhao
 * @date 2021/12/29 18:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ExportTaskSubmitVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "巡检任务id")
	private Long id;

	@ApiModelProperty(value = "巡检池id")
	private Long poolId;

	@ApiModelProperty(value = "任务编号")
	private String code;

	@ApiModelProperty(value = "巡检表名称")
	private String name;

	@ApiModelProperty(value = "所属系统")
	private String systemTypeName;

	@ApiModelProperty(value = "班组")
	private String organizationName;

	@ApiModelProperty(value = "巡检频率: 1.一天1次 2.一周2次 3.一周1次")
	@Excel(name = "巡检频率", width = 15, replace = {"1次/天_1", "2次/周_2", "1次/周_3","单次_4"})
	private Integer tactics;

	@ApiModelProperty(value = "巡检人")
	private String staffName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "提交时间")
	private Date submitTime;

	@ApiModelProperty(value = "站点名称")
	private String stationName;

	@ApiModelProperty(value = "异常状态")
	private Integer warningStatus;

}
