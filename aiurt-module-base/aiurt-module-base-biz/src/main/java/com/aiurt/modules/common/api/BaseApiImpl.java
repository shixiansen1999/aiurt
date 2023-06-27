package com.aiurt.modules.common.api;

import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import com.aiurt.modules.schedule.dto.ScheduleUserWorkDTO;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.schedule.service.IScheduleRecordService;
import com.aiurt.modules.train.task.dto.TrainExperienceDTO;
import com.aiurt.modules.train.task.mapper.BdTrainTaskSignMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @Autowired
    private BdTrainTaskSignMapper bdTrainTaskSignMapper;

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

    @Override
    public List<ScheduleUserWorkDTO> getTodayUserWork(List<String> userIds) {
        return scheduleRecordService.getTodayUserWork(userIds);
    }

    @Override
    public IPage<TrainExperienceDTO> getTrainExperience(Page<TrainExperienceDTO> page, String userId) {
        return bdTrainTaskSignMapper.getTrainExperience(page, userId);
    }
}
