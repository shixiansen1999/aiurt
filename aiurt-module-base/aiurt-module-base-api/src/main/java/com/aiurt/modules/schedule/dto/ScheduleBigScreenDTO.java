package com.aiurt.modules.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1317:09
 */
@Data
public class ScheduleBigScreenDTO {
    @ApiModelProperty("总班组数")
    private Integer teamTotal;
    @ApiModelProperty("总人员数")
    private Integer userTotal;
    @ApiModelProperty("今日当班数")
    private Integer scheduleNum;
}
