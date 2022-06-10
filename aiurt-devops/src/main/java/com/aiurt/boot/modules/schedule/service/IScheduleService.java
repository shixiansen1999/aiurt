package com.aiurt.boot.modules.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.schedule.entity.Schedule;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IScheduleService extends IService<Schedule> {
    public IPage<Schedule> getList(Schedule schedule, Page<Schedule> page);

    void importScheduleExcel(List<Map<Integer, String>> scheduleDate, HttpServletRequest request);
}
