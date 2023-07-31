package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author admin
 */
@Data
public class SysUserPositionCurrentDTO {

    /**
     * 用户所在的上一个车站的连接wifi的mac地址，null值也更新
     */
    @ApiModelProperty(value = "用户所在的上一个车站的连接wifi的mac地址")
    private String bssid;

    /**
     * 新车站编码（如果是换乘站则转换成站点code用来比较）
     */
    @ApiModelProperty(value = "当前用户(app)所在的车站编号")
    private String stationCode;

    /**
     * 原车站编码（用来存储记录）
     */
    @ApiModelProperty(value = "当前用户(app)所在的车站编号")
    private String oldStationCode;
}
