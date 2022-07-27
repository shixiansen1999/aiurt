package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@ApiModel("使用知识库")
@Data
public class UseKnowledgeDTO implements Serializable {

    private static final long serialVersionUID = 1900906591597703864L;

    @ApiModelProperty(value = "故障编码", required = true)
    private String faultCode;
    @ApiModelProperty(value = "知识库id", required = true)
    private String knowledgeId;
}
