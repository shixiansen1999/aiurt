package com.aiurt.modules.common.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBaseApi {

    /**
     * 查询
     *
     * @return
     */
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month);

    /**
     * 查询今日当班人员信息
     *
     * @param orgCodes
     * @param date
     * @return
     */
    List<SysUserTeamDTO> getTodayOndutyDetailNoPage(List<String> orgCodes, Date date);
}
