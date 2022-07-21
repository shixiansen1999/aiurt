package com.aiurt.modules.schedule.mapper;



import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.model.SysUserScheduleModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Repository
public interface ScheduleRecordMapper extends BaseMapper<ScheduleRecord> {

    List<ScheduleRecord> getScheduleRecordBySchedule(@Param("scheduleId") Integer scheduleId);

    List<ScheduleUser> getScheduleUserByDate(@Param("date") String date, @Param("username")String username);

    List<ScheduleRecordModel> getRecordListByUserAndDate(@Param("userId") String userId, @Param("date") String date);

    List<ScheduleRecordModel> getAllScheduleRecordsByMonth(@Param("date") String date,@Param("orgId")String orgId);

    List<LoginUser> getScheduleUserDataByDay(@Param("day") String day, @Param("orgId")String orgId);

    List<ScheduleRecordModel> getRecordListByDay(String date);

    List<ScheduleRecordModel> getRecordListByDayAndUserIds(@Param("date")String date,@Param("userIds")List<String>userIds);

    List<ScheduleRecord> getRecordListInDays(@Param("userId") String userId,@Param("start") String startDate, @Param("end") String endDate);

    List<ScheduleUser> getScheduleUserByDateAndOrgCode(@Param("date") String date,@Param("username")String username,@Param("orgCode") String orgCode);

    List<LoginUser> getDutyUserListByOrgIdsAndDate(@Param("date") String date, @Param("orgIds") List<String>orgIds);

    List<SysUserScheduleModel> getDutyUserByOrgIdAndDate(String orgId, String date);

    //大屏统计分析
    Integer getZhiBanNum(Map map);

    List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(@Param("date") String date, @Param("username") String username, @Param("orgCode") String orgCode, @Param("orgId") String orgId);

    /**
     * 查询
     * @param orgCode
     * @return
     */
    List<LoginUser> userList(@Param("orgCode")String orgCode);

    /**
     * 查询code
     * @param id
     * @return
     */
    List<String> getRoleCodeById(String id);
}
