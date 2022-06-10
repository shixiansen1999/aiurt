package com.aiurt.boot.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class FaultAnalysisReportDTO {


	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
	@ApiModelProperty(value = "故障编号，示例：G101.2109.001")
	@NotBlank(message = "故障编号不能为空")
	private String faultCode;

	/**故障分析*/
	@Excel(name = "故障分析", width = 15)
	@ApiModelProperty(value = "故障分析")
	@NotBlank(message = "故障分析不能为空")
	private String faultAnalysis;

	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
	@ApiModelProperty(value = "故障现象")
	private String faultPhenomenon;

	/**解决方案*/
	@Excel(name = "解决方案", width = 15)
	@ApiModelProperty(value = "解决方案")
	@NotBlank(message = "解决方案不能为空")
	private String solution;

	/**删除状态：0.未删除 1已删除*/
	@Excel(name = "删除状态：0.未删除 1已删除", width = 15)
	@ApiModelProperty(value = "删除状态：0.未删除 1已删除")
	private Integer delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**附件列表*/
	public List<String> urlList;
}
