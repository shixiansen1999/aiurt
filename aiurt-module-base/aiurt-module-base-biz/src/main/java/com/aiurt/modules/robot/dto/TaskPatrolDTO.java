package com.aiurt.modules.robot.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-30 12:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "TaskPatrolDTO", description = "机器人巡检记录查询DTO")
public class TaskPatrolDTO extends TaskExcuteInfo {
    @ApiModelProperty(value = "机器人名称")
    private String robotName;

    @ApiModelProperty(value = "点位名称")
    private String pointName;

    @ApiModelProperty(value = "点位类型")
    private String pointType;

    /**设备名称*/
    @ApiModelProperty(value = "设备名称")
    private String name;

    @ApiModelProperty(value = "设备Id")
    private String deviceId;

    @ApiModelProperty(value = "设备类型")
    @Dict(dictTable = "device_type", dicText = "name", dicCode = "code")
    private String deviceTypeCode;

    @ApiModelProperty(value = "线路")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;

    @ApiModelProperty(value = "站点")
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String stationCode;


}
