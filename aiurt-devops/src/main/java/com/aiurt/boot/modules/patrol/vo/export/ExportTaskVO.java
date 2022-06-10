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
 * @description: ExportTaskVO
 * @author: Mr.zhao
 * D @date: 2021/9/29 22:51
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ExportTaskVO implements Serializable {

	@ApiModelProperty(value = "任务编号")
	@Excel(name = "任务编号", width = 25)
	private String code;

	@ApiModelProperty(value = "巡检表名称")
	@Excel(name = "巡检表名称", width = 40)
	private String name;

	@ApiModelProperty(value = "所属系统")
	@Excel(name = "所属系统", width = 30)
	private String systemTypeName;

	@ApiModelProperty(value = "班组")
	@Excel(name = "班组", width = 20)
	private String organizationName;

	@ApiModelProperty(value = "巡检频率: 1.一天1次 2.一周2次 3.一周1次")
	@Excel(name = "巡检频率", width = 15, replace = {"1次/天_1", "2次/周_2", "1次/周_3","单次_4"})
	private String tactics;

	@ApiModelProperty(value = "巡检人")
	@Excel(name = "巡检人", width = 25)
	private String staffName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "提交时间")
	@Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private Date submitTime;
}
