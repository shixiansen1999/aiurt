package com.aiurt.boot.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

@Data
@ExcelTarget("FaultKnowledgeBaseInputDTO")
public class FaultKnowledgeBaseInputDTO {

    /**删除状态:0.未删除 1已删除*/
    @ApiModelProperty(value = "删除状态：0-未删除 1-已删除")

    private Integer delFlag;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**系统名称*/
    @Excel(name = "系统名称", width = 15)
    @ApiModelProperty(value = "系统编号")
    private String systemName;

    /**故障知识类型*/
    @Excel(name = "故障知识类型", width = 15)
    @ApiModelProperty(value = "故障知识类型")
    private String faultKnowledgeType;

    /**系统编号*/
    @Excel(name = "系统编号", width = 15)
    @ApiModelProperty(value = "系统编号")
    private String systemCode;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**故障原因*/
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultReason;

    /**故障措施/解决方案*/
    @Excel(name = "故障措施", width = 15)
    @ApiModelProperty(value = "故障措施/解决方案")
    private String solution;

    /**关联故障,例:G101.2109.001，G101.2109.002*/
    @Excel(name = "关联故障", width = 15)
    @ApiModelProperty(value = "关联故障")
    private String faultCodes;

    /**浏览次数*/
    @Excel(name = "浏览次数", width = 15)
    @ApiModelProperty(value = "浏览次数")
    private Integer scanNum;
}
