package com.aiurt.modules.faultknowledgebase.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class AnalyzeFaultCauseResDTO implements Serializable {

    private static final long serialVersionUID = -4518108281290861478L;

    @ApiModelProperty(value = "故障原因")
    private String faultCause;

    @ApiModelProperty(value = "出现百分率")
    private String percentage;

    @ApiModelProperty(value = "故障原因id")
    private String id;

    @ApiModelProperty(value = "知识库库id")
    private String knowledgeBaseId;

    @ApiModelProperty("数量")
    private Long num;
}
