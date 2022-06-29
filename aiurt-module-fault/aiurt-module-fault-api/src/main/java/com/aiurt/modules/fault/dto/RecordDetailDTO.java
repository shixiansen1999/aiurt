package com.aiurt.modules.fault.dto;

import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("维修记录")
public class RecordDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

    @ApiModelProperty(value = "故障恢复时长")
    private String recoveryDuration;

    @ApiModelProperty(value = "维修记录")
    private List<RepairRecordDetailDTO> detailList;

    @ApiModelProperty("故障知识库")
    private FaultKnowledgeBase faultKnowledgeBase;
}
