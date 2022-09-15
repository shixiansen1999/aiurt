package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 班组画像-工时详情
 * @date 2022/9/1410:06
 */
@Data
public class TeamWorkingHourDTO {
    @ApiModelProperty("工区名称")
    private String siteName;
    @ApiModelProperty("工区位置")
    private String positionName;
    @ApiModelProperty("站点个数")
    private String stationNum;
    @ApiModelProperty("管辖范围")
    private String jurisdiction;
    @ApiModelProperty("班组人员信息")
    List<TeamUserDTO> teamUserDTOS;
}
