package com.aiurt.modules.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BaseApiImpl implements IBaseApi{

    @Override
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month) {


        return null;
    }
}
