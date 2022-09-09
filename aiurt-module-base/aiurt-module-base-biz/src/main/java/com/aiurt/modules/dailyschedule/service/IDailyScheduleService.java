package com.aiurt.modules.dailyschedule.service;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: 日程安排
 * @Author: aiurt
 * @Date:   2022-09-08
 * @Version: V1.0
 */
public interface IDailyScheduleService extends IService<DailySchedule> {

    /**
     * 查询某一天的日程
     * @param year 年
     * @param month 月
     * @param day 日
     * @return
     */
    List<DailySchedule> queryList(Integer year, Integer month, Integer day);


    /**
     * 获取某年，某月的日常数据，
     * @param year 年
     * @param month 月
     * @return Map key：yyyy/MM/dd value: List<DailySchedule>
     */
    Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month);
}
