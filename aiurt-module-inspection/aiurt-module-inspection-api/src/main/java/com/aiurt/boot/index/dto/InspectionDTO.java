package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1317:23
 */
@Data
public class InspectionDTO {
    @ApiModelProperty("施工数")
    private String teamName;

    @ApiModelProperty("施工数")
    private String stationName;

    @ApiModelProperty("检修任务")
    private String inspectionTask;

    @ApiModelProperty("检修人")
    private String realName;

    @ApiModelProperty("检修时间")
    private Date time;

    @ApiModelProperty(value = "状态")
    private String statusName;
}
