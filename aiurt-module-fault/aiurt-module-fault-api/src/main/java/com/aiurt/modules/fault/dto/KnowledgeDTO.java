package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel(value = "故障方案推荐")
public class KnowledgeDTO implements Serializable {

    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "故障方案ids")
    private String knowledgeIds;

}
