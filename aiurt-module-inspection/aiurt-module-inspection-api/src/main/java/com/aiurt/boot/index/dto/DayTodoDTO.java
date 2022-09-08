package com.aiurt.boot.index.dto;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/612:12
 */
@Data
public class DayTodoDTO {
    @ApiModelProperty("施工数")
    private Integer constructionNum;
    @ApiModelProperty("故障单")
    private Integer faultNum;
    @ApiModelProperty("巡视数")
    private Integer patrolNum;
    @ApiModelProperty("检修数")
    private Integer inspectionNum;
    @ApiModelProperty("日期")
    private String currDate;
    @ApiModelProperty("代办事项")
    List<DailySchedule> dailyScheduleList;

}
