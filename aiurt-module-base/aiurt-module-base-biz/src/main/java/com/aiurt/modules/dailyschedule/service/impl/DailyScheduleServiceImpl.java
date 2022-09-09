package com.aiurt.modules.dailyschedule.service.impl;

import com.aiurt.modules.api.IBaseApi;
import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.mapper.DailyScheduleMapper;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 日程安排
 * @Author: aiurt
 * @Date:   2022-09-08
 * @Version: V1.0
 */
@Service
public class DailyScheduleServiceImpl extends ServiceImpl<DailyScheduleMapper, DailySchedule> implements IDailyScheduleService, IBaseApi {

    /**
     * 查询
     *
     * @param year
     * @param month
     * @return
     */
    @Override
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month) {
        Map<String, List<DailySchedule>> resutlt = new HashMap<>(32);
        return resutlt;
    }
}
