package com.aiurt.boot.modules.fault.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author WangHongTao
 * @Date 2021/11/19
 */
@Data
public class FaultCountParam {

    @ApiModelProperty(value = "线路")
    private String lineCode;

    @ApiModelProperty(value = "站点")
    private String stationCode;

    @ApiModelProperty(value = "系统")
    private String systemCode;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime dayStart;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime dayEnd;
}
