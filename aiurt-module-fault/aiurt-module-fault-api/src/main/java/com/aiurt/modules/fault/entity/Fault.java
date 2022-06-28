package com.aiurt.modules.fault.entity;

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
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
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
public class Fault implements Serializable {
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
    @NotBlank(message = "所属专业不能为空")
    private String majorCode;


	/**专业子系统编码*/
	@Excel(name = "专业子系统编码", width = 15)
    @ApiModelProperty(value = "专业子系统编码")
    private String subSystemCode;


	/**影响范围*/
	@Excel(name = "影响范围", width = 15)
    @ApiModelProperty(value = "影响范围")
    @Length(max = 255, message = "影响范围长度不能超过255")
    private String scope;

	/**线路编码*/
	@Excel(name = "故障位置-线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true)
    private String lineCode;

	/**站点*/
	@Excel(name = "故障位置-站所编码", width = 15)
    @ApiModelProperty(value = "站点",  required = true)
    private String stationCode;

	/**位置*/
	@Excel(name = "故障位置-位置编码", width = 15)
    @ApiModelProperty(value = "位置")
    private String stationPositionCode;

	/**故障发生时间*/
	@Excel(name = "故障发生时间", width = 15, format = "yyyy-MM-dd HH:mm")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    @NotNull(message = "请填写故障发生时间")
    private Date happenTime;

	/**故障现象*/
	@Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象",  required = true)
    @NotBlank(message = "请填写故障现象!")
    @Length(max = 255, message = "故障现象长度不能超过255")
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
	@Excel(name = "接报人", width = 15)
    @ApiModelProperty(value = "接报人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String receiveUserName;

	/**接报时间*/
	@Excel(name = "接报时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
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
    @Dict(dictTable = "fault_level", dicCode = "level_code", dicText = "type_name")
    private String faultLevel;

    @ApiModelProperty(value = "故障分类")
    @Dict(dictTable = "fault_type", dicCode = "type_code", dicText = "level_name")
	private String faultTypeCode;

	/**审批驳回原因*/
	@Excel(name = "审批驳回原因", width = 15)
    @ApiModelProperty(value = "审批驳回原因")
    private String approvalRejection;

	@TableField(exist = false)
    @ApiModelProperty("故障设备类表")
	private List<FaultDevice> faultDeviceList;

	@ApiModelProperty("推荐的故障知识库id, 逗号隔开")
    @TableField(exist = false)
	private String knowledgeBaseId;

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

    @ApiModelProperty("被指派人/领取人")
    private String appointUserName;

    @ApiModelProperty("挂起原因")
    private String hangUpReason;

    /**线路名称*/
    @Excel(name = "故障位置-线路名称", width = 15)
    @ApiModelProperty(value = "线路名称", required = true)
    @TableField(exist = false)
    private String lineName;

    /**站点名称*/
    @Excel(name = "故障位置-站所名称", width = 15)
    @ApiModelProperty(value = "站点名称",  required = true)
    @TableField(exist = false)
    private String stationName;

    /**位置名称*/
    @Excel(name = "故障位置-位置名称", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "位置名称")
    private String stationPositionName;

    /**专业名称*/
    @Excel(name = "专业名称", width = 15)
    @ApiModelProperty(value = "专业名称", required = true)
    @TableField(exist = false)
    private String majorName;

    /**专业子系统名称*/
    @Excel(name = "专业子系统名称", width = 15)
    @ApiModelProperty(value = "专业子系统名称")
    @TableField(exist = false)
    private String subSystemName;

}
