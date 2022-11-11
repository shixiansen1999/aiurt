package com.aiurt.modules.schedule.mapper;


import com.aiurt.modules.schedule.dto.ScheduleRecordDTO;
import com.aiurt.modules.schedule.dto.SysTotalTeamDTO;
import com.aiurt.modules.schedule.dto.SysUserScheduleDTO;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.aiurt.modules.schedule.model.SysUserScheduleModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: schedule_record
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Repository
public interface ScheduleRecordMapper extends BaseMapper<ScheduleRecord> {

    List<ScheduleRecord> getScheduleRecordBySchedule(@Param("scheduleId") Integer scheduleId);

    List<ScheduleUser> getScheduleUserByDate(@Param("date") String date, @Param("username") String username);

    List<ScheduleRecordModel> getRecordListByUserAndDate(@Param("userId") String userId, @Param("date") String date);

    List<ScheduleRecordModel> getMySchedule(@Param("date") String date,@Param("userId") String userId);

    List<ScheduleRecordModel> getAllScheduleRecordsByMonth(@Param("date") String date, @Param("orgId") String orgId,@Param("text") String text);

    List<LoginUser> getScheduleUserDataByDay(@Param("day") String day, @Param("orgId") String orgId);

    List<ScheduleRecordModel> getRecordListByDay(String date);

    List<ScheduleRecordModel> getRecordListByDayAndUserIds(@Param("date") String date, @Param("userIds") List<String> userIds);

    List<ScheduleRecord> getRecordListInDays(@Param("userId") String userId, @Param("start") String startDate, @Param("end") String endDate);

    List<ScheduleUser> getScheduleUserByDateAndOrgCode(@Param("date") String date, @Param("username") String username, @Param("orgCode") String orgCode);

    List<LoginUser> getDutyUserListByOrgIdsAndDate(@Param("date") String date, @Param("orgIds") List<String> orgIds);

    List<SysUserScheduleModel> getDutyUserByOrgIdAndDate(String orgId, String date);

    //大屏统计分析
    Integer getZhiBanNum(Map map);

    List<ScheduleUser> getScheduleUserByDateAndOrgCodeAndOrgId(@Param("date") String date, @Param("username") String username, @Param("orgCode") String orgCode, @Param("orgId") String orgId);

    /**
     * 查询
     *
     * @param orgCode
     * @return
     */
    List<LoginUser> userList(@Param("orgCode") String orgCode);

    /**
     * 查询code
     *
     * @param id
     * @return
     */
    List<String> getRoleCodeById(String id);

    /**
     * 根据日期条件查询班次情况
     *
     * @param page
     * @param scheduleRecordDTO
     * @return
     */
    List<SysUserScheduleDTO> getStaffOnDuty(@Param("page") Page<SysUserScheduleDTO> page, @Param("scheduleRecordDTO") ScheduleRecordDTO scheduleRecordDTO);

    /**
     * 查询今日当班人列表
     *
     * @param page
     * @param orgCode
     * @param orgCodes
     * @param date
     * @return
     */
    List<SysUserTeamDTO> getTodayOndutyDetail(@Param("page") Page<SysUserTeamDTO> page, @Param("orgCode") String orgCode, @Param("orgCodes") List<String> orgCodes, @Param("date") Date date);

    /**
     * 查询总人员列表
     *
     * @param orgCodes
     * @param page
     * @param orgCode
     * @return
     */
    List<SysUserTeamDTO> getUserByDepIds(@Param("orgCodes") List<String> orgCodes, @Param("page") Page<SysUserTeamDTO> page, @Param("orgCode") String orgCode);

    /**
     * 获取大屏的班组信息-点击总班组数
     *
     * @param page     分页参数
     * @param orgCodes 组织机构
     * @return
     */
    List<SysTotalTeamDTO> getTotalTeamDetail(@Param("page") Page<SysTotalTeamDTO> page, @Param("orgCodes") List<String> orgCodes);

    /**
     * 查询今日当班人列表(无分页)
     *
     * @param orgCodes
     * @param date
     * @return
     */
    List<SysUserTeamDTO> getTodayOndutyDetailNoPage(@Param("orgCodes") List<String> orgCodes, @Param("date") Date date);
}
