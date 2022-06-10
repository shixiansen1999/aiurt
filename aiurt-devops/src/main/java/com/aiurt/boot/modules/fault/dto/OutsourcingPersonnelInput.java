package com.aiurt.boot.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
@Data
@ExcelTarget("OutsourcingPersonnelInput")
public class OutsourcingPersonnelInput {
	/**人员名称*/
	@Excel(name = "人员名称", width = 15)
    @ApiModelProperty(value = "人员名称")
	private  String  name;

	/**所属单位*/
	@Excel(name = "所属单位", width = 15)
    @ApiModelProperty(value = "所属单位")
	private  String  company;

	/**职位名称*/
	@Excel(name = "职位名称", width = 15)
    @ApiModelProperty(value = "职位名称")
	private  String  position;

	/**所属专业系统*/
	@Excel(name = "所属专业系统", width = 15)
	@ApiModelProperty(value = "所属专业系统")
	private String systemName;

	@ApiModelProperty(value = "所属专业系统编号")
	private String systemCode;

	/**联系方式*/
	@Excel(name = "联系方式", width = 15)
	@ApiModelProperty(value = "联系方式")
	private  String  connectionWay;

	/**施工证编号*/
	@Excel(name = "施工证编号", width = 15)
	@ApiModelProperty(value = "施工证编号")
	private  String  certificateCode;

}
