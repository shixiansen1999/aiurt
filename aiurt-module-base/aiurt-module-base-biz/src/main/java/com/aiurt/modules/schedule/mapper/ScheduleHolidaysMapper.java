package com.aiurt.modules.schedule.mapper;

import java.util.List;

import com.aiurt.modules.schedule.entity.ScheduleHolidays;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: schedule_holidays
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ScheduleHolidaysMapper extends BaseMapper<ScheduleHolidays> {

    List<ScheduleHolidays> getListByMonth(@Param("date") String date);
}
