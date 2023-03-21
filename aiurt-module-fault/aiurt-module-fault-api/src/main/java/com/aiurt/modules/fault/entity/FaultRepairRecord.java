package com.aiurt.modules.fault.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 维修记录
 * @Author: aiurt
 * @Date:   2022-06-28
 * @Version: V1.0
 */
@Data
@TableName("fault_repair_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault_repair_record对象", description="维修记录")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaultRepairRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

	/**故障分类*/
	@Excel(name = "故障分类", width = 15)
    @ApiModelProperty(value = "故障分类")
    private String faultModeCode;
	/**故障级别*/
	@Excel(name = "故障级别", width = 15)
    @ApiModelProperty(value = "故障级别")
    private String faultLevelCode;
	/**指派人*/
	@Excel(name = "指派人/故障负责人", width = 15)
    @ApiModelProperty(value = "指派人/故障负责人")
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
	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;
	/**故障分析*/
	@Excel(name = "故障分析", width = 15)
    @ApiModelProperty(value = "故障分析")
    private String faultAnalysis;
	/**维修措施*/
	@Excel(name = "维修措施", width = 15)
    @ApiModelProperty(value = "处理情况/维修措施")
    private String maintenanceMeasures;
	/**问题解决状态*/
	@Excel(name = "问题解决状态", width = 15)
    @ApiModelProperty(value = "问题解决状态")
    private Integer solveStatus;
	/**未解决备注*/
	@Excel(name = "未解决备注", width = 15)
    @ApiModelProperty(value = "未解决备注")
    private String unSloveRemark;


	/**接收任务时间*/
	@Excel(name = "接收任务时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接收任务时间")
    private Date receviceTime;

	/**开始维修时间*/
	@Excel(name = "开始维修时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始维修时间")
    private Date startTime;

	/**维修完成时间*/
	@Excel(name = "维修完成时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

	/**到达现场时间*/
	@Excel(name = "到达现场时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "到达现场时间")
    private Date arriveTime;

	/**故障维修时长*/
	@Excel(name = "故障维修时长", width = 15)
    @ApiModelProperty(value = "故障维修时长")
    private Integer repairDuration;

	/**维修响应时长*/
	@Excel(name = "维修响应时长", width = 15)
    @ApiModelProperty(value = "维修响应时长")
    private Integer responseDuration;
	/**故障恢复时长*/
	@Excel(name = "故障恢复时长", width = 15)
    @ApiModelProperty(value = "故障恢复时长")
    private Integer recoveryDuration;
	/**挂起原因*/
	@Excel(name = "挂起原因", width = 15)
    @ApiModelProperty(value = "挂起原因")
    private String hangupReason;

    /**
     * 申请挂起时间
     */
    @ApiModelProperty(value = "申请挂起时间")
	private Date reqHangupTime;

	/**附件*/
	@Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private String filePath;
	/**工作票编号*/
	@Excel(name = "工作票编号", width = 15)
    @ApiModelProperty(value = "工作票编号")
    private String workTicketCode;



	/**故障编号*/
	@Excel(name = "故障编号", width = 15)
    @ApiModelProperty(value = "故障编号")
    private String faultCode;

    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**删除标志*/
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    @ApiModelProperty(value = "指派时间")
    private Date assignTime;

    @ApiModelProperty("使用的解决方案ID")
    private String knowledgeId;

    @ApiModelProperty(value = "指派附件或者领取附件")
    private String assignFilePath;

    /**
     * 拒绝指派时间
     */
    @ApiModelProperty(value = "拒绝指派时间")
    private Date refuseAssignTime;

    /**
     * 拒收指派说明
     */
    @ApiModelProperty(value = "拒收指派说明")
    private String refuseAssignRemark;

    /**
     * 挂起审批说明
     */
    @ApiModelProperty(value = "挂起审批说明")
    private String approvalHangUpRemark;

    /**
     * 挂起审批时间
     */
    @ApiModelProperty(value = "挂起审批时间")
    private Date approvalHangUpTime;

    @ApiModelProperty(value = "挂起审批人")
    private String approvalHangUpUser;

    @ApiModelProperty(value = "挂起审批结果")
    private Integer approvalHangUpResult;

    @ApiModelProperty(value = "工作票路径")
    private String workTickPath;

    @ApiModelProperty(value = "提交时间")
    @TableField(exist = false)
    private Date commitTime;


    @ApiModelProperty(value = "签名路径")
    private String signPath;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;

    @ApiModelProperty(value = "处理方式:0维修，1委外维修，2委外送修")
    private Integer processing;
}
