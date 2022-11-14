package com.aiurt.modules.schedule.mapper;


import com.aiurt.modules.schedule.entity.ScheduleLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface ScheduleLogMapper extends BaseMapper<ScheduleLog> {

    List<ScheduleLog> queryPageList(@Param("page")Page<ScheduleLog> page, @Param("scheduleLog") ScheduleLog scheduleLog);
}
