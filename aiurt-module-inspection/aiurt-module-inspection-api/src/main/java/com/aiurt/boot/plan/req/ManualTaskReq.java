package com.aiurt.boot.plan.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author wgp
 * @Title: 手工下发查询参数
 * @Description:
 * @date 2022/7/616:59
 */
@Data
public class ManualTaskReq {

    @ApiModelProperty(value = "检修计划单号")
    private java.lang.String code;

    @ApiModelProperty(value = "检修周期类型")
    private java.lang.Integer type;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "检修任务开始时间")
    private java.util.Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "检修任务结束时间")
    private java.util.Date endTime;

    @ApiModelProperty(value = "检修任务状态")
    private java.lang.Integer status;

    @ApiModelProperty(value = "组织机构code集合")
    private String orgList;

    @ApiModelProperty(value = "站点code集合")
    private String stationList;
}
