package com.aiurt.boot.modules.fault.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: swsc
 * 故障知识库查询参数列表
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class FaultKnowledgeBaseParam {

    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象")
    private String faultPhenomenon;

    /**
     * 故障原因
     */
    @Excel(name = "故障原因", width = 15)
    @ApiModelProperty(value = "故障原因")
    private String faultReason;

    /**
     * 故障措施
     */
    @Excel(name = "故障措施", width = 15)
    @ApiModelProperty(value = "故障措施")
    private String solution;

    /**
     * typeId
     */
    @Excel(name = "typeId", width = 15)
    @ApiModelProperty(value = "typeId")
    private Integer typeId;


    /**
     * typeId
     */
    @Excel(name = "分支类型", width = 15)
    @ApiModelProperty(value = "分支类型")
    private Integer faultType;
}
