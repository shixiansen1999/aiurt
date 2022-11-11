package com.aiurt.modules.schedule.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.schedule.entity.ScheduleLog;
import com.aiurt.modules.schedule.mapper.ScheduleLogMapper;
import com.aiurt.modules.schedule.service.IScheduleLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleLogServiceImpl extends ServiceImpl<ScheduleLogMapper, ScheduleLog> implements IScheduleLogService {


    @Autowired
    private ScheduleLogMapper scheduleLogMapper;


    @Override
    public IPage<ScheduleLog> queryPageList(Page<ScheduleLog> page, ScheduleLog scheduleLog) {
        Page<ScheduleLog> pageList = new Page<>();
        List<ScheduleLog> scheduleLogs = scheduleLogMapper.queryPageList(page, scheduleLog);
        if (CollUtil.isNotEmpty(scheduleLogs)) {
            for (ScheduleLog log : scheduleLogs) {
                if (StrUtil.isEmpty(log.getSourceItemName()) && ObjectUtil.isEmpty(log.getSourceItemId())) {
                    log.setShiftRecord("由休息调整为" + log.getTargetItemName());
                } else if (StrUtil.isEmpty(log.getTargetItemName()) && ObjectUtil.isEmpty(log.getTargetItemId())) {
                    log.setShiftRecord("由" + log.getSourceItemName() + "调整为休息");
                } else {
                    log.setShiftRecord("由" + log.getSourceItemName() + "调整为" + log.getTargetItemName());
                }
            }
        }
        pageList.setRecords(scheduleLogs);
        return pageList;
    }
}
