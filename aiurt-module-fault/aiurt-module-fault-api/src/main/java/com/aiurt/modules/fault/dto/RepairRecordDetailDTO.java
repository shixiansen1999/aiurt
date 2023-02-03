package com.aiurt.modules.fault.dto;

import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */
@Data
@ApiModel("维修记录详情")
public class RepairRecordDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("维修记录id")
    private String id;

    @ApiModelProperty(value = "维修负责人")
    private String appointUserName;

    @ApiModelProperty(value = "维修负责人名称")
    private String appointRealName;

    @ApiModelProperty(value = "作业类型")
    private String workType;

    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;

    @ApiModelProperty(value = "问题解决状态")
    private Integer solveStatus;

    @ApiModelProperty(value = "问题解决状态名")
    private String solveStatusName;

    /**接收任务时间*/
    @Excel(name = "接收任务时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "接收任务时间")
    private Date receviceTime;

    /**开始维修时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始维修时间")
    private Date startTime;

    /**维修完成时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

    /**到达现场时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "到达现场时间")
    private Date arriveTime;

    /**附件*/
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private String filePath;

    @ApiModelProperty(value = "附件具体信息")
    private List<SysAttachment> sysAttachmentList;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;


    /**故障分析*/
    @ApiModelProperty(value = "故障分析")
    private String faultAnalysis;

    @ApiModelProperty(value = "处理情况/维修措施")
    private String maintenanceMeasures;


    @ApiModelProperty(value = "参与人")
    private List<FaultRepairParticipants> participantsList;


    /**故障维修时长*/
    @ApiModelProperty(value = "故障维修时长")
    private String repairDuration;

    /**维修响应时长*/
    @ApiModelProperty(value = "维修响应时长")
    private String responseDuration;

    @ApiModelProperty(value = "签名路径")
    private String signPath;


    @ApiModelProperty(value = "工作票图片路径")
    private String workTickPath;

    @ApiModelProperty(value = "工作票号")
    private String workTicketCode;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象")
    private String symptoms;

}
