package com.aiurt.modules.dailyschedule.mapper;

import java.util.List;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 日程安排
 * @Author: aiurt
 * @Date:   2022-09-08
 * @Version: V1.0
 */
public interface DailyScheduleMapper extends BaseMapper<DailySchedule> {


    /**
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @param userId 用户账号
     * @return
     */
    List<DailySchedule> queryDailyScheduleList(@Param("year")Integer year, @Param("month")Integer month,
                                               @Param("day")Integer day, @Param("userId")String userId);
}
