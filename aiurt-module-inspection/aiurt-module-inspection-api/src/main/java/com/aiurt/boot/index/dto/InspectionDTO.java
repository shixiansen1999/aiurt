package com.aiurt.boot.index.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1317:23
 */
@Data
public class InspectionDTO {
    @ApiModelProperty("班组")
    private String teamName;

    @ApiModelProperty("站点")
    private String stationName;

    @ApiModelProperty("检修任务")
    private String inspectionTask;

    @ApiModelProperty("检修人")
    private String realName;

    @ApiModelProperty("检修时间")
    private String time;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty("所属周")
    private Integer weeks;

    @ApiModelProperty("单号")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String code;

}
