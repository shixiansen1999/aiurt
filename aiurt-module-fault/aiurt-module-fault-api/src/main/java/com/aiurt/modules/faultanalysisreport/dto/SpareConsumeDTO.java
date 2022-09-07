package com.aiurt.modules.faultanalysisreport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("备件消耗top5")
public class SpareConsumeDTO implements Serializable {

    private static final long serialVersionUID = 6393896912289547937L;

    @ApiModelProperty("名称")
    private String spareName;

    @ApiModelProperty("数量")
    private String num;
}
