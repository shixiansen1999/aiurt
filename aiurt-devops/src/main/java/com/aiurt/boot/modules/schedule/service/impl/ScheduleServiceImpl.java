package com.aiurt.boot.modules.schedule.service.impl;

import com.swsc.copsms.modules.schedule.entity.Schedule;
import com.swsc.copsms.modules.schedule.mapper.ScheduleMapper;
import com.swsc.copsms.modules.schedule.service.IScheduleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

}
