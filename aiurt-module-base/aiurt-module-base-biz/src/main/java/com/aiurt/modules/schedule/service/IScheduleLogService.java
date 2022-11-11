package com.aiurt.modules.schedule.service;


import com.aiurt.modules.schedule.entity.ScheduleLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IScheduleLogService extends IService<ScheduleLog> {

    IPage<ScheduleLog> queryPageList(Page<ScheduleLog> page, ScheduleLog scheduleLog);
}
