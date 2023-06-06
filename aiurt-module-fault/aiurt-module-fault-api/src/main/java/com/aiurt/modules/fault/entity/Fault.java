package com.aiurt.modules.fault.entity;

import com.aiurt.common.aspect.annotation.*;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.faultknowledgebase.dto.AnalyzeFaultCauseResDTO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Data
@TableName("fault")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fault对象", description="fault")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fault extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;


	/**故障报修编码*/
	@Excel(name = "故障报修编码", width = 15)
    @ApiModelProperty(value = "故障报修编码")
    private String code;


	/**专业编码*/
	@Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码", required = true)
    @NotBlank(message = "请选择所属专业")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    @MajorFilterColumn
    private String majorCode;


	/**专业子系统编码*/
	@Excel(name = "专业子系统编码", width = 15)
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;


	/**影响范围*/
	@Excel(name = "影响范围", width = 15)
    @ApiModelProperty(value = "影响范围")
    @Length(max = 255, message = "影响范围长度不能超过255")
    private String scope;

	/**线路编码*/
	@Excel(name = "故障位置-线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    @LineFilterColumn
    private String lineCode;

    @ApiModelProperty(value = "线路名称", required = true)
    @TableField(exist = false)
    private String lineName;

	/**站点*/
	@Excel(name = "故障位置-站所编码", width = 15)
    @ApiModelProperty(value = "站点",  required = true)
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    @NotBlank(message = "请选择位置")
    @StaionFilterColumn
    private String stationCode;

	/**位置*/
	@Excel(name = "故障位置-位置编码", width = 15)
    @ApiModelProperty(value = "位置")
    @Dict(dictTable = "cs_station_position", dicText = "position_name", dicCode = "position_code")
    private String stationPositionCode;

	/**故障发生时间*/
	@Excel(name = "故障发生时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    @NotNull(message = "请填写故障发生时间")
    private Date happenTime;

	/**故障现象*/
	@Excel(name = "故障现象分类", width = 15)
    @ApiModelProperty(value = "故障现象分类编码",  required = true)
    @NotBlank(message = "故障现象分类!")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;

	/**报修人*/
	@Excel(name = "报修人", width = 15)
    @ApiModelProperty(value = "报修人账号")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String faultApplicant;

	/**报修部门*/
	@Excel(name = "报修部门", width = 15)
    @ApiModelProperty(value = "报修部门")
    @Dict(dictTable = "sys_depart", dicCode = "org_code", dicText = "depart_name")
    private String faultApplicantDept;

	/**接报人*/
	@Excel(name = "接报人/填报人", width = 15)
    @ApiModelProperty(value = "填报人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String receiveUserName;

	/**接报时间*/
	@Excel(name = "接报时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接报时间")
    private Date receiveTime;

	/**附件*/
	@Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private String path;


	/**报修编号*/
	@Excel(name = "报修编号", width = 15)
    @ApiModelProperty(value = "关联流程编号/报修编号")
    private String repairCode;

	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "fault_status")
    private Integer status;

    @ApiModelProperty(value = "状态,多状态查询,逗号隔开")
    @TableField(exist = false)
	private String statusCondition;


	/**紧急程度*/
	@Excel(name = "紧急程度", width = 15)
    @ApiModelProperty(value = "fault_urgency,紧急程度,0:低,1:中,2高")
    @Dict(dicCode = "fault_urgency")
    private Integer urgency;

	/**是否影响行车*/
	@Excel(name = "是否影响行车", width = 15)
    @ApiModelProperty(value = "fault_yn,是否影响行车,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectDrive;

	/**是否影响客运服务*/
	@Excel(name = "是否影响客运服务", width = 15)
    @ApiModelProperty(value = "fault_yn,是否影响客运服务,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectPassengerService;


	/**是否停止服务*/
	@Excel(name = "是否停止服务", width = 15)
    @Dict(dicCode = "fault_yn")
    @ApiModelProperty(value = "fault_yn,是否停止服务,1:是,0否,2未知",  required = true)
    private Integer isStopService;

	/**通知人员*/
	@Excel(name = "通知人员", width = 15)
    @ApiModelProperty(value = "通知人员")
    private String noticeUser;

	/**通知时间*/
	@Excel(name = "通知时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "通知时间")
    private Date noticeTime;
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
    @DeptFilterColumn
    private String sysOrgCode;

	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

	/**报修方式*/
	@Excel(name = "报修方式", width = 15)
    @ApiModelProperty(value = "报修方式",example = "")
    @NotBlank(message = "请选择报修方式")
    @Dict(dicCode = "fault_mode_code")
    private String faultModeCode;

	/**故障级别*/
	@Excel(name = "故障级别", width = 15)
    @ApiModelProperty(value = "故障级别")
    @Dict(dictTable = "fault_level", dicCode = "code", dicText = "name")
    private String faultLevel;

    @ApiModelProperty(value = "故障分类")
    @Dict(dictTable = "fault_type", dicCode = "code", dicText = "name")
	private String faultTypeCode;

	/**审批驳回原因*/
	@Excel(name = "审批驳回原因", width = 15)
    @ApiModelProperty(value = "审批驳回原因")
    private String approvalRejection;

	@TableField(exist = false)
    @ApiModelProperty("故障设备类表")
	private List<FaultDevice> faultDeviceList;



	@ApiModelProperty(value = "yn, 是否委外 1:是,0否", required = true)
    @Dict(dicCode = "yn")
	private Integer isOutsource;

    /**设备编码*/
    @Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码", required = true)
    @TableField(exist = false)
    private String deviceCode;

    /**设备编码*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称", required = true)
    @TableField(exist = false)
    private String deviceName;

	@ApiModelProperty(value = "作废用户")
	private String cancelUserName;

    @ApiModelProperty(value = "作废时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    @ApiModelProperty("指派人")
    private String assignUserName;

    @ApiModelProperty("指派时间")
    private Date assignTime;

    @ApiModelProperty("被指派人/领取人/责任人")
    @Dict(dicCode = "username", dicText = "realname", dictTable = "sys_user")
    private String appointUserName;

    @ApiModelProperty("挂起原因")
    private String hangUpReason;

    /**维修完成时间*/
    @Excel(name = "维修完成时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

    @ApiModelProperty("设备查询条件")
    @TableField(exist = false)
    private String devicesIds;

    @ApiModelProperty(value = "作废说明")
    private String cancelRemark;

    @ApiModelProperty(value = "故障报修时长 min")
    private Long duration;

    @ApiModelProperty(value = "app, 故障上报设备编码，逗号隔开")
    @TableField(exist = false)
    private String deviceCodes;

    @ApiModelProperty(value = "权重等级")
    @TableField(exist = false)
    private Integer weight;

    @ApiModelProperty(value = "是否重新指派, 1是, 0否")
    @TableField(exist = false)
    private Integer signAgainFlag;

    @ApiModelProperty(value = "最近一次审核时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    @ApiModelProperty(value = "审核通过时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date approvalPassTime;

    @ApiModelProperty(value = "上报审核人员")
    private String approvalUserName;

    @ApiModelProperty(value = "超时时长")
    @TableField(exist = false)
    private String overTime;

    @ApiModelProperty(value = "超时类型")
    @TableField(exist = false)
    private String overType;

    @ApiModelProperty(value = "班组名称")
    @TableField(exist = false)
    private String orgName;

    @ApiModelProperty(value = "班主负责人")
    @TableField(exist = false)
    private String orgChargeName;

    @ApiModelProperty(value = "故障原因分类名称")
    @TableField(exist = false)
    private String phenomenonTypeName;

    @ApiModelProperty(value = "是否存在故障分析")
    @TableField(exist = false)
    private Boolean isFaultAnalysisReport;

    @ApiModelProperty(value = "抄送人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String remindUserName;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;

    @ApiModelProperty("推荐使用的故障知识库id, 逗号隔开")
    @Deprecated
    private String knowledgeBaseIds;

    @ApiModelProperty("故障现象id，从故障想象模板接口回填")
    private String knowledgeId;

    @ApiModelProperty(value = "故障描述")
    private String faultMark;

    @ApiModelProperty(value = "故障详细位置")
    private String detailLocation;
    @ApiModelProperty(value = "是否是自己的故障任务")
    @TableField(exist = false)
    private Boolean isFault;
    @ApiModelProperty(value = "是否是调度列表的故障下发")
    @TableField(exist = false)
    private Boolean isFaultExternal;

    @ApiModelProperty(value = "故障原因")
    @TableField(exist = false)
    private List<AnalyzeFaultCauseResDTO> analyzeFaultCauseResDTOList;

    @ApiModelProperty(value = "完成状态")
    @Dict(dicCode = "fault_state")
    private Integer state;
    /**
     * 用户账号字段(人员画像历史维修记录列表(更多)用到)
     */
    @TableField(exist = false)
    private String username;
}

