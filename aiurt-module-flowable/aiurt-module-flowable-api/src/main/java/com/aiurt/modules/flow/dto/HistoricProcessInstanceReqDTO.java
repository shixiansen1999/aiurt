package com.aiurt.modules.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fgw
 */
@Data
public class HistoricProcessInstanceReqDTO implements Serializable {
    private static final long serialVersionUID = -4781378336805291461L;

    @ApiModelProperty(value = "流程")
    private String processDefinitionName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startTime;

    /**
     * 介绍时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;

    private Integer pageNo;

    private Integer pageSize;

    @ApiModelProperty("发起人")
    private String loginName;
}
