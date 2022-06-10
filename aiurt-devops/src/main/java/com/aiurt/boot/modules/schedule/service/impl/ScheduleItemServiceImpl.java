package com.aiurt.boot.modules.schedule.service.impl;

import com.swsc.copsms.modules.schedule.entity.ScheduleItem;
import com.swsc.copsms.modules.schedule.mapper.ScheduleItemMapper;
import com.swsc.copsms.modules.schedule.service.IScheduleItemService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: schedule_item
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleItemServiceImpl extends ServiceImpl<ScheduleItemMapper, ScheduleItem> implements IScheduleItemService {

}
