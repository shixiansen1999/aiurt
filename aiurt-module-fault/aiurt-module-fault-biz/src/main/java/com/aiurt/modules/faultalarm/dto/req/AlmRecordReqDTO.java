package com.aiurt.modules.faultalarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author:wgp
 * @create: 2023-06-05 09:46
 * @Description: 告警记录请求DTO
 */
@Data
public class AlmRecordReqDTO {
    @ApiModelProperty(value = "站点编码")
    private String stationCode;

    @ApiModelProperty(value = "子系统")
    private String subSystemCode;

    @ApiModelProperty(value = "处理状态")
    private Integer state;

    @ApiModelProperty(value = "告警发生开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startAlmTime;

    @ApiModelProperty(value = "告警发生结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endAlmTime;
}
