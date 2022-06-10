package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/27 17:11
 */
@Data
public class StatisticsQueryVO {
    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private String endTime;
    @ApiModelProperty(value = "班组id")
    private String teamId;
    @ApiModelProperty(value = "姓名")
    private String userName;
}
