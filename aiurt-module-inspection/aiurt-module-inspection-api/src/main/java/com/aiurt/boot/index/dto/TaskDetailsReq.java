package com.aiurt.boot.index.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description: 检修任务详情
 * @date 2022/9/517:08
 */
@Data
public class TaskDetailsReq {
    @ApiModelProperty(value = "开始时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "开始时间不能为空")
    private java.util.Date startTime;
    @ApiModelProperty(value = "结束时间",required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @ApiModelProperty(value = "任务状态")
    private Integer status;
    @ApiModelProperty(value = "检修任务状态")
    private Integer taskStatus;
    @ApiModelProperty(value = "站点code")
    private String stationCode;
    @ApiModelProperty(value = "线路code")
    private String lineCode;
    @ApiModelProperty(value = "pageNo")
    private Integer pageNo = 1;
    @ApiModelProperty(value = "pageSize")
    private Integer pageSize = 10;
}
