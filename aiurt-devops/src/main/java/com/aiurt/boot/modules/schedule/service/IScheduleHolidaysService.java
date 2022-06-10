package com.aiurt.boot.modules.schedule.service;

import com.aiurt.boot.modules.schedule.entity.ScheduleHolidays;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_holidays
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
public interface IScheduleHolidaysService extends IService<ScheduleHolidays> {
    List<ScheduleHolidays> getListByMonth(String date);

    void importHolidayExcel(List<Map<Integer, String>> data, HttpServletRequest request);
}
