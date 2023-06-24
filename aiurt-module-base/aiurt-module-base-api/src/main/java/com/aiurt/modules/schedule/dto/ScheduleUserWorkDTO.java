package com.aiurt.modules.schedule.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUserWorkDTO {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private Date date;
    /**
     * 上班时间
     */
    @ApiModelProperty(value = "上班时间")
    private Date startTime;
    /**
     * 下班时间
     */
    @ApiModelProperty(value = "下班时间")
    private Date endTime;
    /**
     * 值班状态 1值班中，0休息
     */
    @ApiModelProperty(value = "值班状态 1值班中，0休息")
    private String work;
}
