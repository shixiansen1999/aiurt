package com.aiurt.modules.workticket.entity;

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
import java.util.List;

/**
 * @Description: bd_work_ticket
 * @Author: aiurt
 * @Date:   2022-10-08
 * @Version: V1.0
 */
@Data
@TableName("bd_work_ticket")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bd_work_ticket对象", description="bd_work_ticket")
public class BdWorkTicket implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**工作票号*/
	@Excel(name = "工作票号", width = 15)
    @ApiModelProperty(value = "工作票号")
    private String workTicketCode;
	/**站所*/
	@Excel(name = "站所", width = 15)
    @ApiModelProperty(value = "站所")
    @Dict(dictTable = "cs_station", dicCode = "id" , dicText = "station_name")
    private String station;
	/**作业地点及内容*/
	@Excel(name = "作业地点及内容", width = 15)
    @ApiModelProperty(value = "作业地点及内容")
    private String workAddressContent;
	/**填票人*/
	@Excel(name = "填票人", width = 15)
    @ApiModelProperty(value = "填票人")
    private String ticketFiller;
	/**工作负责人*/
	@Excel(name = "工作负责人", width = 15)
    @ApiModelProperty(value = "工作负责人")
    private String workLeader;
	/**工作票有效开始时间*/
	@Excel(name = "工作票有效开始时间", width = 15)
    @ApiModelProperty(value = "工作票有效开始时间")
    private String workStartTime;
	/**作业时间*/
	@Excel(name = "作业时间", width = 15)
    @ApiModelProperty(value = "作业时间")
    private String operationTime;
	/**工作票有效结束时间*/
	@Excel(name = "工作票有效结束时间", width = 15)
    @ApiModelProperty(value = "工作票有效结束时间")
    private String workEndTime;
	/**工作组成员*/
	@Excel(name = "工作组成员", width = 15)
    @ApiModelProperty(value = "工作组成员")
    private String workPartner;
	/**断开的断路器和断开的隔离开关*/
	@Excel(name = "断开的断路器和断开的隔离开关", width = 15)
    @ApiModelProperty(value = "断开的断路器和断开的隔离开关")
    private String circuitBreakerSwitch;
	/**安装接地线（或接地刀闸）的位置*/
	@Excel(name = "安装接地线（或接地刀闸）的位置", width = 15)
    @ApiModelProperty(value = "安装接地线（或接地刀闸）的位置")
    private String groundWireStation;
	/**装设防护栅、悬挂标示牌的位置*/
	@Excel(name = "装设防护栅、悬挂标示牌的位置", width = 15)
    @ApiModelProperty(value = "装设防护栅、悬挂标示牌的位置")
    private String signboardStation;
	/**注意作业地点附近有电的设备*/
	@Excel(name = "注意作业地点附近有电的设备", width = 15)
    @ApiModelProperty(value = "注意作业地点附近有电的设备")
    private String electricEquipmentOl;
	/**其他安全措施*/
	@Excel(name = "其他安全措施", width = 15)
    @ApiModelProperty(value = "其他安全措施")
    private String safetyMeasures;
	/**流程实例id*/
	@Excel(name = "流程实例id", width = 15)
    @ApiModelProperty(value = "流程实例id")
    @TableField(exist = false)
    private String procInstId;
	/**已完成断开的断路器和断开的隔离开关*/
	@Excel(name = "已完成断开的断路器和断开的隔离开关", width = 15)
    @ApiModelProperty(value = "已完成断开的断路器和断开的隔离开关")
    private String completedCircuitBreakerSwitch;
	/**已完成安装接地线（或接地刀闸）的位置*/
	@Excel(name = "已完成安装接地线（或接地刀闸）的位置", width = 15)
    @ApiModelProperty(value = "已完成安装接地线（或接地刀闸）的位置")
    private String completedGroundWireStation;
	/**已完成装设防护栅、悬挂标示牌的位置*/
	@Excel(name = "已完成装设防护栅、悬挂标示牌的位置", width = 15)
    @ApiModelProperty(value = "已完成装设防护栅、悬挂标示牌的位置")
    private String completedSignboardStation;
	/**已完成注意作业地点附近有电的设备*/
	@Excel(name = "已完成注意作业地点附近有电的设备", width = 15)
    @ApiModelProperty(value = "已完成注意作业地点附近有电的设备")
    private String completedElectricEquipmentOl;
	/**已完成其他安全措施*/
	@Excel(name = "已完成其他安全措施", width = 15)
    @ApiModelProperty(value = "已完成其他安全措施")
    private String completedSafetyMeasures;
	/**工作负责人(确认)*/
	@Excel(name = "工作负责人(确认)", width = 15)
    @ApiModelProperty(value = "工作负责人(确认)")
    private String workLeaderSign;
	/**工作负责人签字时间*/
	@Excel(name = "工作负责人签字时间", width = 15)
    @ApiModelProperty(value = "工作负责人签字时间")
    private String workLeaderSignTime;
	/**签发人*/
	@Excel(name = "签发人", width = 15)
    @ApiModelProperty(value = "签发人")
    private String signeUser;
	/**签发人签字时间*/
	@Excel(name = "签发人签字时间", width = 15)
    @ApiModelProperty(value = "签发人签字时间")
    private String signeUserTime;
	/**电调*/
	@Excel(name = "电调", width = 15)
    @ApiModelProperty(value = "电调")
    private String powerDispatcherName;
	/**电调签字时间*/
	@Excel(name = "电调签字时间", width = 15)
    @ApiModelProperty(value = "电调签字时间")
    private String powerDispatcherTime;
	/**工作负责人审核意见*/
	@Excel(name = "工作负责人审核意见", width = 15)
    @ApiModelProperty(value = "工作负责人审核意见")
    private String workLeaderMessage;
	/**工作负责人是否同意*/
	@Excel(name = "工作负责人是否同意", width = 15)
    @ApiModelProperty(value = "工作负责人是否同意")
    private Integer workLeaderAgree;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private Integer state;
	/**签发人是否同意*/
	@Excel(name = "签发人是否同意", width = 15)
    @ApiModelProperty(value = "签发人是否同意")
    private Integer signeUserAgree;
	/**签发人意见*/
	@Excel(name = "签发人意见", width = 15)
    @ApiModelProperty(value = "签发人意见")
    private String signeUserMessage;
	/**电调是否同意*/
	@Excel(name = "电调是否同意", width = 15)
    @ApiModelProperty(value = "电调是否同意")
    private Integer powerDispatcherAgree;
	/**电调审核意见*/
	@Excel(name = "电调审核意见", width = 15)
    @ApiModelProperty(value = "电调审核意见")
    private String powerDispatcherMessage;
	/**图片路径*/
	@Excel(name = "图片路径", width = 15)
    @ApiModelProperty(value = "图片路径")
    private String picturePath;
	/**归档电调员*/
	@Excel(name = "归档电调员", width = 15)
    @ApiModelProperty(value = "归档电调员")
    private String filePowerDispatcher;
	/**归档编号*/
	@Excel(name = "归档编号", width = 15)
    @ApiModelProperty(value = "归档编号")
    private String fileWorkTicketCode;
	/**开始时间*/
	@Excel(name = "开始时间", width = 15)
    @ApiModelProperty(value = "开始时间")
    private String fileStartTime;
	/**实际时间*/
	@Excel(name = "实际时间", width = 15)
    @ApiModelProperty(value = "实际时间")
    private String fileActualTime;
	/**工作许可人1*/
	@Excel(name = "工作许可人1", width = 15)
    @ApiModelProperty(value = "工作许可人1")
    private String workPermitHolder1;
	/**安全确认开始时间*/
	@Excel(name = "安全确认开始时间", width = 15)
    @ApiModelProperty(value = "安全确认开始时间")
    private String fileConfirmActualTime;
	/**工作负责人*/
	@Excel(name = "工作负责人", width = 15)
    @ApiModelProperty(value = "工作负责人")
    private String fileWorkLeader;
	/**记录*/
	@Excel(name = "记录", width = 15)
    @ApiModelProperty(value = "记录")
    private String fileRecord;
	/**批准人（工作负责人或签发人）*/
	@Excel(name = "批准人（工作负责人或签发人）", width = 15)
    @ApiModelProperty(value = "批准人（工作负责人或签发人）")
    private String approvedUser;
	/**归档电调员1*/
	@Excel(name = "归档电调员1", width = 15)
    @ApiModelProperty(value = "归档电调员1")
    private String filePowerDispatcher1;
	/**延迟时间*/
	@Excel(name = "延迟时间", width = 15)
    @ApiModelProperty(value = "延迟时间")
    private String delayTime;
	/**工作负责人1*/
	@Excel(name = "工作负责人1", width = 15)
    @ApiModelProperty(value = "工作负责人1")
    private String fileWorkLeader1;
	/**工作许可人2*/
	@Excel(name = "工作许可人2", width = 15)
    @ApiModelProperty(value = "工作许可人2")
    private String workPermitHolder2;
	/**间断后开始时间*/
	@Excel(name = "间断后开始时间", width = 15)
    @ApiModelProperty(value = "间断后开始时间")
    private String interruptedTime;
	/**工作负责人2*/
	@Excel(name = "工作负责人2", width = 15)
    @ApiModelProperty(value = "工作负责人2")
    private String fileWorkLeader2;
	/**工作结束时间*/
	@Excel(name = "工作结束时间", width = 15)
    @ApiModelProperty(value = "工作结束时间")
    private String endTime;
	/**工作负责人3*/
	@Excel(name = "工作负责人3", width = 15)
    @ApiModelProperty(value = "工作负责人3")
    private String fileWorkLeader3;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**工作负责人4*/
	@Excel(name = "工作负责人4", width = 15)
    @ApiModelProperty(value = "工作负责人4")
    private String fileWorkLeader4;
	/**组*/
	@Excel(name = "组", width = 15)
    @ApiModelProperty(value = "组")
    private String fileTeam;
	/**归档电调员2*/
	@Excel(name = "归档电调员2", width = 15)
    @ApiModelProperty(value = "归档电调员2")
    private String filePowerDispatcher2;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15)
    @ApiModelProperty(value = "结束时间")
    private String finalEndTime;
	/**工作许可人3*/
	@Excel(name = "工作许可人3", width = 15)
    @ApiModelProperty(value = "工作许可人3")
    private String workPermitHolder3;
	/**工作条件*/
	@Excel(name = "工作条件", width = 15)
    @ApiModelProperty(value = "工作条件")
    private String workCondition;

    /**任务名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String workTicketName;
    /**线路号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "线路号")
    private java.lang.String lineId;
    /**类型*/
    @TableField(exist = false)
    @ApiModelProperty(value = "类型")
    private java.lang.String workTicketType;

    @ApiModelProperty(value = "流程任务")
    @TableField(exist = false)
    private String taskId;

    @ApiModelProperty(value = "共计人数")
    @TableField(exist = false)
    private Integer count;

    @ApiModelProperty(value = "工作组成员")
    @TableField(exist = false)
    private List<String> resNames;

    @ApiModelProperty(value = "工作票类型")
    @TableField(exist = false)
    private String ticketType;




}
