package com.aiurt.modules.faultproducereportlinedetail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 专业故障清单
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
@Data
@TableName("fault_produce_report_line_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_produce_report_line_detail对象", description="专业故障清单")
public class FaultProduceReportLineDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**线路编码*/
	@Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
	/**线路名称*/
	@Excel(name = "线路名称", width = 15)
    @ApiModelProperty(value = "线路名称")
    private String lineName;
	/**故障编码*/
	@Excel(name = "故障编码", width = 15)
    @ApiModelProperty(value = "故障编码")
    private String faultCode;
	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;
	/**处理情况*/
	@Excel(name = "处理情况", width = 15)
    @ApiModelProperty(value = "处理情况")
    private String maintenanceMeasures;
	/**是否影响行车*/
	@Excel(name = "是否影响行车", width = 15)
    @ApiModelProperty(value = "是否影响行车")
    private Integer affectDrive;
	/**是否影响客运服务*/
	@Excel(name = "是否影响客运服务", width = 15)
    @ApiModelProperty(value = "是否影响客运服务")
    private Integer affectPassengerService;
	/**是否停止服务*/
	@Excel(name = "是否停止服务", width = 15)
    @ApiModelProperty(value = "是否停止服务")
    private Integer isStopService;
	/**站点编码*/
	@Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
	/**站点名称*/
	@Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
	/**	生产日报_线路统计故障数id*/
	@Excel(name = "	生产日报_线路统计故障数id", width = 15)
    @ApiModelProperty(value = "	生产日报_线路统计故障数id")
    private String faultProduceReportLineId;
}
