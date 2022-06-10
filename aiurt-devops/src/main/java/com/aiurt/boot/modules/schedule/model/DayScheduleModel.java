package com.aiurt.boot.modules.schedule.model;

import com.aiurt.boot.modules.schedule.vo.ScheduleCalendarVo;
import lombok.Data;

import java.util.List;

@Data
public class DayScheduleModel {
    private List<String> holidays;
    List<ScheduleCalendarVo> voList;
}
