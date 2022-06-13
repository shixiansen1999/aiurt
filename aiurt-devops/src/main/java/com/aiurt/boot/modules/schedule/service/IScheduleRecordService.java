package com.aiurt.boot.modules.schedule.service;

import com.aiurt.boot.modules.schedule.entity.ScheduleRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.boot.modules.schedule.model.ScheduleUser;
import org.jeecg.common.system.vo.LoginUser;


import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
public interface IScheduleRecordService extends IService<ScheduleRecord> {
    List<ScheduleRecord> getScheduleRecordBySchedule(Integer scheduleId);

    List<ScheduleUser> getScheduleUserByDate(String date,String username);

    List<ScheduleRecordModel> getRecordListByUserAndDate(String userId, String date);

    List<ScheduleRecordModel> getAllScheduleRecordsByMonth(String date,String orgId);

    List<LoginUser> getScheduleUserDataByDay(String day, String orgId);

    List<ScheduleRecordModel> getRecordListByDay(String date);

    List<ScheduleRecordModel> getRecordListByDayAndUserIds(String date,List<String>userIds);

    List<ScheduleRecord> getRecordListInDays(String userId,String startDate,String endDate);

    List<ScheduleUser> getScheduleUserByDateAndOrgCode(String date,String username, String orgCode);

    List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(String date, String username, String orgCode, String orgId);

    /**
     * 统计分析
     * @param map
     * @return
     */
    Integer getZhiBanNum(Map map);


}
