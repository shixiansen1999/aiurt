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
 * 导出巡检池vc
 *
 * @description: ExportPoolVO
 * @author: Mr.zhao
 * @date: 2021/9/27 15:16
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ExportManuallyAddVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "任务编号")
	@Excel(name = "任务编号", width = 25)
	private String code;

	@ApiModelProperty(value = "巡检表名称")
	@Excel(name = "巡检表名称", width = 40)
	private String name;

	@ApiModelProperty(value = "班组")
	@Excel(name = "班组", width = 20)
	private String organizationName;

	@ApiModelProperty(value = "站点名称")
	@Excel(name = "站点", width = 25)
	private String lineName;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "巡检要求完成时间")
	@Excel(name = "巡检要求完成时间", width = 20, format = "yyyy-MM-dd")
	private Date executionTime;

	@ApiModelProperty("备注")
	@Excel(name = "备注", width = 25)
	private String note;

	@ApiModelProperty(value = "巡检人")
	@Excel(name = "巡检人", width = 25)
	private String staffName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "完成时间")
	@Excel(name = "完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private Date submitTime;
}
