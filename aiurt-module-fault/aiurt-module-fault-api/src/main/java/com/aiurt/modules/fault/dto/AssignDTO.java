package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel("指派")
public class AssignDTO implements Serializable {

    @ApiModelProperty("故障编号")
    @NotBlank(message = "请选择故障编号")
    private String faultCode;

    @ApiModelProperty("作业类型")
    private String caWorkCode;

    @ApiModelProperty("作业人员")
    private String operators;

    @ApiModelProperty("计划令编码")
    private String planCode;

    @ApiModelProperty("附件")
    private String filepath;
}
