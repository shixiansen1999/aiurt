package com.aiurt.boot.plan.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/7/1211:32
 */
@Data
public class SelectPlanReq {

    @ApiModelProperty(value = "开始时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private java.util.Date startTime;
    @ApiModelProperty(value = "结束时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "多个状态,用,隔开")
    private String statuStr;
    @ApiModelProperty(value = "作业类型")
    private Integer workType;
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    @ApiModelProperty(value = "pageNo")
    private Integer pageNo = 1;
    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 30;
}
