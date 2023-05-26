package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:计划站所信息
 * @author hlq
 * @date 2023-05-24
 */
@Data
public class StationPlanDTO {

    /**站所编号*/
    @ApiModelProperty(value = "站所编号")
    private String stationCode;
    /**站所编号*/
    @ApiModelProperty(value = "站所名称")
    private String stationName;
    /**线路编号*/
    @ApiModelProperty(value = "线路编号")
    private String lineCode;
    /**位置编号*/
    @ApiModelProperty(value = "位置编号")
    private String positionCode;
    @ApiModelProperty(value = "计划数")
    private Long planNum;
    @ApiModelProperty(value = "计划-完成数")
    private Long planFinishNum;
    @ApiModelProperty(value = "计划-未完成数")
    private Long unPlanFinishNum;
    @ApiModelProperty(value = "审核数")
    private Long auditNum;
    @ApiModelProperty(value = "审核-已审核数")
    private Long auditFinishNum;
    @ApiModelProperty(value = "审核-未审核数")
    private Long unAuditFinishNum;

}
