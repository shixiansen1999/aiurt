package com.aiurt.boot.modules.fault.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_repair_record对象", description="故障维修记录表")
public class FaultRepairDTO {

	/**主键id，自动递增*/
	@TableId(type= IdType.AUTO)
	@ApiModelProperty(value = "主键id，自动递增")
	private Long id;

	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
	@ApiModelProperty(value = "故障编号，示例：G101.2109.001")
	private String faultCode;

	/**参与人id集合*/
	@Excel(name = "参与人id集合", width = 15)
	@ApiModelProperty(value = "参与人id集合")
	private String participateIds;

	/**委外人员人id集合*/
	@Excel(name = "委外人员id集合", width = 15)
	@ApiModelProperty(value = "委外人员id集合")
	private String outsourcingIds;

	/**故障分析*/
	@Excel(name = "故障分析", width = 15)
	@ApiModelProperty(value = "故障分析")
	private String faultAnalysis;

	/**维修措施*/
	@Excel(name = "维修措施", width = 15)
	@ApiModelProperty(value = "维修措施")
	private String maintenanceMeasures;

	/**问题解决状态：0-未解决 1-已解决*/
	@Excel(name = "问题解决状态：0-未解决 1-已解决", width = 15)
	@ApiModelProperty(value = "问题解决状态：0-未解决 1-已解决")
	private Integer    solveStatus;

	/**维修结束时间*/
	@Excel(name = "维修结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "维修结束时间")
	private Date overTime;

	/**附件列表*/
	@Excel(name = "附件列表", width = 15)
	@ApiModelProperty(value = "附件列表")
	public List<String> urlList;
}
