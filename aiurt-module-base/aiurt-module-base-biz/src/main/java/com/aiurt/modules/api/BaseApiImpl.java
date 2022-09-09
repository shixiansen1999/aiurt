package com.aiurt.modules.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseApiImpl implements IBaseApi{

    @Autowired
    private IDailyScheduleService dailyScheduleService;

    @Override
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month) {


        return dailyScheduleService.queryDailyScheduleList(year,month);
    }
}
