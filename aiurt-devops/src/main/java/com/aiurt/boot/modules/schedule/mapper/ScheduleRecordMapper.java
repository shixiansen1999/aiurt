package com.aiurt.boot.modules.schedule.mapper;

import java.util.List;
import java.util.Map;

import com.aiurt.boot.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.boot.modules.schedule.model.ScheduleUser;
import com.aiurt.boot.modules.schedule.model.SysUserScheduleModel;
import com.aiurt.boot.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.schedule.entity.ScheduleRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Repository
public interface ScheduleRecordMapper extends BaseMapper<ScheduleRecord> {

    List<ScheduleRecord> getScheduleRecordBySchedule(@Param("scheduleId") Integer scheduleId);

    List<ScheduleUser> getScheduleUserByDate(@Param("date") String date,@Param("username")String username);

    List<ScheduleRecordModel> getRecordListByUserAndDate(@Param("userId") String userId, @Param("date") String date);

    List<ScheduleRecordModel> getAllScheduleRecordsByMonth(@Param("date") String date,@Param("orgId")String orgId);

    List<SysUser> getScheduleUserDataByDay(@Param("day") String day, @Param("orgId")String orgId);

    List<ScheduleRecordModel> getRecordListByDay(String date);

    List<ScheduleRecordModel> getRecordListByDayAndUserIds(@Param("date")String date,@Param("userIds")List<String>userIds);

    List<ScheduleRecord> getRecordListInDays(@Param("userId") String userId,@Param("start") String startDate, @Param("end") String endDate);

    List<ScheduleUser> getScheduleUserByDateAndOrgCode(@Param("date") String date,@Param("username")String username,@Param("orgCode") String orgCode);

    List<SysUser> getDutyUserListByOrgIdsAndDate(@Param("date") String date, @Param("orgIds") List<String>orgIds);

    List<SysUserScheduleModel> getDutyUserByOrgIdAndDate(String orgId, String date);

    //大屏统计分析
    Integer getZhiBanNum(Map map);

    List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(@Param("date") String date, @Param("username") String username, @Param("orgCode") String orgCode, @Param("orgId") String orgId);
}
