package com.aiurt.boot.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.swsc.copsms.modules.fault.entity.FaultAnalysisReport;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public class FaultAnalysisReportDTO extends FaultAnalysisReport {

	/**主键id，自动递增*/
	private Long id;
	/**故障编号，示例：G101.2109.001*/
	private String faultCode;
	/**故障分析*/
	private String faultAnalysis;
	/**解决方案*/
	private String solution;
	/**删除状态：0.未删除 1已删除*/
	private Integer delFlag;
	/**创建人*/
	private String createBy;
	/**修改人*/
	private String updateBy;
	/**创建时间，CURRENT_TIMESTAMP*/
	private Date createTime;
	/**修改时间，根据当前时间戳更新*/
	private Date updateTime;
	/**附件列表*/
	public List<String> urlList;
}
