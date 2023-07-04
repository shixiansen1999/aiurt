package com.aiurt.modules.schedule.service;

import com.aiurt.modules.schedule.entity.Schedule;
import com.aiurt.modules.schedule.entity.ScheduleItem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SysParamModel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    Result<?> importScheduleExcel(List<Map<Integer, String>> scheduleDate, HttpServletResponse response) throws IOException;

    Result<Schedule> add(Schedule schedule);
    /**
     * 排班表模板下载
     * @param response
     * @throws IOException
     */
    void exportTemplateXls(HttpServletResponse response)throws IOException;

    /**
     * 根据配置的班次名称获取班次
     * @param sysParamModel
     * @return
     */
    ScheduleItem getItemByParam(SysParamModel sysParamModel);
}
