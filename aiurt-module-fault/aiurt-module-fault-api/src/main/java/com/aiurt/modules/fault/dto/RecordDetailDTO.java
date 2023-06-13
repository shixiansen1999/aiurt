package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.LineFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel("维修记录")
public class RecordDetailDTO extends DictEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("状态")
    @Dict(dicCode = "fault_status")
    private Integer status;


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

    /**线路编码*/
    @Excel(name = "故障位置-线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    @LineFilterColumn
    private String lineCode;

    @ApiModelProperty(value = "线路名称", required = true)
    @TableField(exist = false)
    private String lineName;
}
