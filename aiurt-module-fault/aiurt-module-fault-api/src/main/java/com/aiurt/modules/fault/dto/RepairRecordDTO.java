package com.aiurt.modules.fault.dto;

import com.aiurt.modules.fault.entity.FaultRepairParticipants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class RepairRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("参与人")
    private List<FaultRepairParticipants> participantsList;

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


}
