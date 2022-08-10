package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "站点联动对象", description = "站点联动对象")
public class PatrolStationDTO {
    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    /**
     * 巡检工单列表
     */
    @ApiModelProperty(value = "巡检工单列表")
    private List<PatrolBillDTO> billInfo;
}
