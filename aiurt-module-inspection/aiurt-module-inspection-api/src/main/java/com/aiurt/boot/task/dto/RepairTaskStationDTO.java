package com.aiurt.boot.task.dto;
/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022/10/31
 * @time: 11:34
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RepairTaskStationDTO {
    /**
     * 站点编号
     */
    @Excel(name = "站点编号", width = 15)
    @ApiModelProperty(value = "站点编号")
    private String stationCode;
    /**
     * 站点名称
     */
    @Excel(name = "站点名称", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
}
