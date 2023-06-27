package com.aiurt.modules.common.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.schedule.dto.ScheduleUserWorkDTO;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.train.task.dto.TrainExperienceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

    /**
     * 查询用户今日是否值班
     *
     * @param userIds
     * @return
     */
    List<ScheduleUserWorkDTO> getTodayUserWork(List<String> userIds);

    /**
     * 获取用户的培训经历
     *
     * @param page
     * @param userId
     * @return
     */
    IPage<TrainExperienceDTO> getTrainExperience(Page<TrainExperienceDTO> page, String userId);
}
