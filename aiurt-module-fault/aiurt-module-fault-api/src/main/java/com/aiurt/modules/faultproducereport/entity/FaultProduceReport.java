package com.aiurt.modules.faultproducereport.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
@Data
@TableName("fault_produce_report")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_produce_report对象", description="生产日报")
public class FaultProduceReport implements Serializable {
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
	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;
	/**统计日期*/
	@Excel(name = "统计日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "统计日期")
    private Date statisticsDate;
	/**开始统计日期*/
	@Excel(name = "开始统计日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始统计日期")
    private Date startTime;
	/**截止统计日期*/
	@Excel(name = "截止统计日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "截止统计日期")
    private Date endTime;
	/**提交人*/
	@Excel(name = "提交人", width = 15)
    @ApiModelProperty(value = "提交人")
    private String submitUserName;
	/**提交时间*/
	@Excel(name = "提交时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;
	/**故障总数*/
	@Excel(name = "故障总数", width = 15)
    @ApiModelProperty(value = "故障总数")
    private Integer totalNum;
	/**延误次数*/
	@Excel(name = "延误次数", width = 15)
    @ApiModelProperty(value = "延误次数")
    private Integer delayNum;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "fault_produce_report_state")
    private Integer state;
    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id")
    @TableField(exist = false)
    private String processInstanceId;
    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    @TableField(exist = false)
    private String taskId;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String taskName;
    /**
     * 模板key，流程标识
     */
    @ApiModelProperty(value = "模板key，流程标识")
    @TableField(exist = false)
    private String modelKey;
}
