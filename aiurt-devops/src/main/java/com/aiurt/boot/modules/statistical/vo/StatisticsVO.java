package com.aiurt.boot.modules.statistical.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/20 14:08
 */
@Data
public class StatisticsVO {
    @NotNull(message = "开始时间不能为空")
    private String startTime;
    @NotNull(message = "结束时间不能为空")
    private String endTime;
    @ApiModelProperty(value = "班组Id")
    private String teamId;
    @ApiModelProperty(value = "姓名")
    private String userName;
}
