package com.aiurt.modules.dailyschedule.service;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
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
     * @return
     */
    List<DailySchedule> queryList(Date addTime);


    /**
     * 获取某年，某月的日常数据，
     * @param year 年
     * @param month 月
     * @return Map key：yyyy/MM/dd value: List<DailySchedule>
     */
    Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month);

    /**
     * 查询发送人是自己的数据
     * @return
     */
    List<DailySchedule> queryOwnlist(Date addTime);
}
