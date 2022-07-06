package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */
@Data
@ApiModel("维修记录1")
public class RepairRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "故障维修编码")
    private String faultCode;

    @ApiModelProperty("维修记录id")
    private String id;

    @ApiModelProperty("参与人")
    private List<FaultRepairParticipants> participantsList;

    @ApiModelProperty(value = "参与人用户id")
    private String userIds;

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
    private List<DeviceChangeDTO>  deviceChangeList;



    @ApiModelProperty(value = "易耗品")
    private List<DeviceChangeDTO> consumableList;



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

}
