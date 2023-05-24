package com.aiurt.modules.faultcausesolution.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * 故障原因解决方案对象
 */
@ApiModel(value = "故障原因解决方案对象", description = "故障原因解决方案对象")
@Data
public class FaultCauseSolutionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 虚拟ID
     */
    @ApiModelProperty(value = "虚拟ID")
    private String id;
    /**
     * 故障知识库ID
     */
    @ApiModelProperty(value = "故障知识库ID")
    private String knowledgeBaseId;
    /**
     * 故障原因
     */
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultCause;
    /**
     * 解决方案
     */
    @Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "解决方案")
    private String solution;
    /**
     * 备件信息
     */
    @ApiModelProperty(value = "备件信息")
    private List<FaultSparePartDTO> spareParts;
}
