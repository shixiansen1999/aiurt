package com.aiurt.modules.common.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BaseApiImpl implements IBaseApi {

    @Autowired
    private IDailyScheduleService dailyScheduleService;
    @Autowired
    private IScheduleRecordService scheduleRecordService;

    @Override
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month) {
        return dailyScheduleService.queryDailyScheduleList(year, month);
    }

    /**
     * 查询今日当班人员信息
     *
     * @param orgCodes
     * @param date
     * @return
     */
    @Override
    public List<SysUserTeamDTO> getTodayOndutyDetailNoPage(List<String> orgCodes, Date date) {
        return scheduleRecordService.getTodayOndutyDetailNoPage(orgCodes, date);
    }
}
