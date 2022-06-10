package com.aiurt.boot.modules.schedule.service.impl;

import com.swsc.copsms.modules.schedule.entity.ScheduleLog;
import com.swsc.copsms.modules.schedule.mapper.ScheduleLogMapper;
import com.swsc.copsms.modules.schedule.service.IScheduleLogService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleLogServiceImpl extends ServiceImpl<ScheduleLogMapper, ScheduleLog> implements IScheduleLogService {

}
