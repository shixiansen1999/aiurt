package com.aiurt.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author WangHongTao
 * @Date 2021/11/21
 */

@Data
public class FaultDeviceResult {

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "故障发生时间，CURRENT_TIMESTAMP")
    private Date createTime;

    @Excel(name = "故障编号，示例：G101.2109.001", width = 15)
    @ApiModelProperty(value = "故障编号，示例：G101.2109.001")
    private String code;

    @ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
    private Integer faultLevel;

    @ApiModelProperty(value = "故障级别：1-普通故障 2-重大故障")
    private String faultLevelDesc;

    @ApiModelProperty(value = "系统")
    private String systemName;

    @ApiModelProperty(value = "车站")
    private String stationName;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "报修人")
    private String createBy;

    @ApiModelProperty(value = "解决人")
    private String solveBy;

}
