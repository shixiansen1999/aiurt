package com.aiurt.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.fault.dto
 * @className: FaultReportDTO
 * @author: life-0
 * @date: 2022/10/9 16:35
 * @description: TODO
 * @version: 1.0
 */
@Data
public class FaultReportDTO {
    @ApiModelProperty(value = "人员ID")
    String  userId;
    @ApiModelProperty(value = "班组Code")
    String  orgCode;
    @ApiModelProperty(value = "班组Id")
    String  orgId;
    Long  num;
    @ApiModelProperty(value = "故障总工时")
    BigDecimal failureTime;
    @ApiModelProperty(value = "施工人数")
    Integer constructorsNum;
    @ApiModelProperty(value = "施工工时")
    BigDecimal constructionHours;
}
