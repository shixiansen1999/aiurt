package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.aiurt.modules.faultcauseusagerecords.entity.FaultCauseUsageRecords;
import com.aiurt.modules.faultknowledgebase.dto.AnalyzeFaultCauseResDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */
@Data
@ApiModel("维修记录1")
public class RepairRecordDTO extends DictEntity implements Serializable  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障维修编码")
    private String faultCode;

    @ApiModelProperty("维修记录id")
    private String id;

    @ApiModelProperty("参与人")
    private List<FaultRepairParticipants> participantsList;

    @ApiModelProperty(value = "账号")
    private String users;

    @ApiModelProperty(value = "参与人员名称")
    private String userNames;

    /**到达现场时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "到达现场时间")
    private Date arriveTime;

    /**维修措施*/
    @ApiModelProperty(value = "处理情况/维修措施")
    private String maintenanceMeasures;

    /**故障分析*/
    @ApiModelProperty(value = "原因分析/故障分析")
    private String faultAnalysis;

    /**
     * 工作票编码
     */
    @ApiModelProperty(value = "工作票编码")
    private String workTickCode;

    /**
     * 工作票图片路径
     */
    @ApiModelProperty(value = "工作票图片路径")
    private String workTickPath;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果")
    @Dict(dicCode = "fault_result_code")
    private Integer solveStatus;

    /**
     * 未解决备注
     */
    @ApiModelProperty(value = "未解决备注")
    private String unSloveRemark;

    /**
     * 附件
     */
    @ApiModelProperty(value = "附件")
    private String filePath;

    /**
     * 组件更换
     */
    @ApiModelProperty(value = "组件更换")
    private List<SparePartStockDTO>  deviceChangeList;



    @ApiModelProperty(value = "易耗品")
    private List<SparePartStockDTO> consumableList;

    @ApiModelProperty(value = "app: 设备面好")
    private String deviceCodes;


    @ApiModelProperty(value = "设备信息")
    private List<FaultDevice> deviceList;

    @ApiModelProperty(value = "是否需要指派,1是,0否")
    private Integer assignFlag;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "指派时间")
    private Date assignTime;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始维修时间")
    private Date startTime;

    @ApiModelProperty(value = "解决方案id")
    private String knowledgeId;

    @ApiModelProperty(value = "总解决方案记录数")
    private Long total;

    @ApiModelProperty(value = "签名路径")
    private String signPath;

    @ApiModelProperty(value = "工作票号")
    private String workTicketCode;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;

    @ApiModelProperty(value = "处理方式:0维修，1委外维修，2委外送修")
    @Dict(dicCode = "usage_status")
    private Integer processing;

    /**线路编码*/
    @ApiModelProperty(value = "线路编码", required = true)
    private String lineCode;

    @ApiModelProperty(value = "故障设备：站点")
    private String stationCode;

    @ApiModelProperty(value = "故障设备：位置")
    private String stationPositionCode;

    @ApiModelProperty(value = "使用的解决方案")
    private List<FaultCauseUsageRecords> recordsList;

    @ApiModelProperty(value = "排查方法")
    private String method;

    @ApiModelProperty(value = "故障级别")
    @Dict(dictTable = "fault_level", dicCode = "code", dicText = "name")
    private String faultLevel;

    @ApiModelProperty(value = "故障原因")
    private String faultCauseSolution;

    @ApiModelProperty(value = "故障原因分析百分比")
    @TableField(exist = false)
    private List<AnalyzeFaultCauseResDTO> analyzeFaultCauseResDTOList;

    /**专业编码*/
    @ApiModelProperty(value = "专业编码", required = true)
    @NotBlank(message = "请选择所属专业")
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;


    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private String subSystemCode;

    private String deviceTypeCode;

    /**是否是信号故障（0信号故障；1非信号故障）*/
    @ApiModelProperty(value = "是否是信号故障（0信号故障；1非信号故障）")
    @TableField(exist = false)
    @Dict(dicCode = "is_signal_fault")
    private Integer isSignalFault;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;
    
    /**是否影响行车*/
    @Excel(name = "是否影响行车", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "fault_yn,是否影响行车,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectDrive;

    /**是否影响客运服务*/
    @Excel(name = "是否影响客运服务", width = 15)
    @TableField(exist = false)
    @ApiModelProperty(value = "fault_yn,是否影响客运服务,1:是,0否,2未知",  required = true)
    @Dict(dicCode = "fault_yn")
    private Integer affectPassengerService;


    /**是否停止服务*/
    @Excel(name = "是否停止服务", width = 15)
    @TableField(exist = false)
    @Dict(dicCode = "fault_yn")
    @ApiModelProperty(value = "fault_yn,是否停止服务,1:是,0否,2未知",  required = true)
    private Integer isStopService;
}
