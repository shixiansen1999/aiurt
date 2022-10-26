package com.aiurt.modules.schedule.service;

import com.aiurt.modules.schedule.entity.Schedule;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletResponse;
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

    Result<?> importScheduleExcel(List<Map<Integer, String>> scheduleDate, HttpServletResponse response);
}
