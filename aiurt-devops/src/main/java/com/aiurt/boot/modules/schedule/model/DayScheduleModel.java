package com.aiurt.modules.schedule.model;

import com.aiurt.modules.schedule.vo.ScheduleCalendarVo;
import lombok.Data;

import java.util.List;

@Data
public class DayScheduleModel {
    private List<String> holidays;
    List<ScheduleCalendarVo> voList;
}
