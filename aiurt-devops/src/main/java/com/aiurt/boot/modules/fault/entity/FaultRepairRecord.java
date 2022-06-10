package com.aiurt.boot.modules.fault.entity;

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

import java.util.Date;
import java.util.List;

/**
 * @Description: 故障维修记录表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("fault_repair_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fault_repair_record对象", description="故障维修记录表")
public class FaultRepairRecord {

	/**主键id，自动递增*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id，自动递增")
	private Long id;
	/**故障编号，示例：G101.2109.001*/
	@Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
	private String faultCode;
	/**指派人*/
	@Excel(name = "指派人", width = 15)
    @ApiModelProperty(value = "指派人")
	private String appointUserId;
	/**作业类型*/
	@Excel(name = "作业类型", width = 15)
    @ApiModelProperty(value = "作业类型")
	private String workType;
	/**计划令编码*/
	@Excel(name = "计划令编码", width = 15)
    @ApiModelProperty(value = "计划令编码")
	private String planOrderCode;
	/**计划令图片*/
	@Excel(name = "计划令图片", width = 15)
    @ApiModelProperty(value = "计划令图片")
	private String planOrderImg;
	/**参与人id集合*/
	@Excel(name = "参与人id集合", width = 15)
    @ApiModelProperty(value = "参与人id集合")
	private String participateIds;
	/**委外人员id集合*/
	@Excel(name = "委外人员id集合", width = 15)
	@ApiModelProperty(value = "委外人员id集合")
	private String outsourcingIds;
	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
	private String faultPhenomenon;
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
	private Integer solveStatus;
	/**状态：0-未提交 1-已提交*/
	@Excel(name = "状态：0-未提交 1-已提交", width = 15)
    @ApiModelProperty(value = "状态：0-未提交 1-已提交")
	private Integer status;
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
	/**维修结束时间*/
	@Excel(name = "维修结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "维修结束时间")
	private Date overTime;
	/**创建时间，CURRENT_TIMESTAMP*/
	@Excel(name = "创建时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间，CURRENT_TIMESTAMP")
	private Date createTime;
	/**修改时间，根据当前时间戳更新*/
	@Excel(name = "修改时间，根据当前时间戳更新", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间，根据当前时间戳更新")
	private Date updateTime;
	List<String> urlList;
}
