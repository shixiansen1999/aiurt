package com.aiurt.common.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
public class FaultRepairRecordResult {

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

	/**指派人*/
	@Excel(name = "指派人", width = 15)
	@ApiModelProperty(value = "指派人")
	private String appointUserName;

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

	/**委外人员姓名集合*/
	@Excel(name = "委外人员姓名集合", width = 15)
	@ApiModelProperty(value = "委外人员姓名集合")
	private String outsourcingNames;

	/**参与人姓名集合*/
	@Excel(name = "参与人姓名集合", width = 15)
	@ApiModelProperty(value = "参与人姓名集合")
	private String participateNames;

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

	/**问题解决状态：0-未解决 1-已解决*/
	@Excel(name = "问题解决状态：0-未解决 1-已解决", width = 15)
	@ApiModelProperty(value = "问题解决状态：0-未解决 1-已解决")
	private String solveStatusDesc;

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

	/**创建人姓名*/
	@Excel(name = "创建人姓名", width = 15)
	@ApiModelProperty(value = "创建人姓名")
	private String createByName;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;

	/**维修结束时间，CURRENT_TIMESTAMP*/
	@Excel(name = "维修结束时间，CURRENT_TIMESTAMP", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "维修结束时间，CURRENT_TIMESTAMP")
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

	/**附件列表*/
	List<String> urlList;

	List<?> deviceList;

	/**签名*/
	@Excel(name = "签名", width = 15)
	@ApiModelProperty(value = "签名")
	List<String> signature;

	/**备件列表*/
	@Excel(name = "备件列表", width = 15)
	@ApiModelProperty(value = "备件列表")
	List<?> list;

	@ApiModelProperty(value = "系统编号")
	private String systemCode;

	@ApiModelProperty(value = "站点编号")
	private String stationCode;

	@ApiModelProperty(value = "线路编号")
	private String lineCode;

	@ApiModelProperty(value = "未解决备注")
	private String remark;
}
