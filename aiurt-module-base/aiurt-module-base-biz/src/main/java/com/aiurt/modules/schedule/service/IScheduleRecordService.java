package com.aiurt.modules.schedule.service;


import com.aiurt.modules.schedule.dto.ScheduleBigScreenDTO;
import com.aiurt.modules.schedule.dto.ScheduleRecordDTO;
import com.aiurt.modules.schedule.dto.SysUserScheduleDTO;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.schedule.entity.ScheduleRecord;
import com.aiurt.modules.schedule.model.ScheduleRecordModel;
import com.aiurt.modules.schedule.model.ScheduleUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;


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

    List<ScheduleUser> getScheduleUserByDate(String date, String username);

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

    /**
     * 根据日期查询班次情况
     * @param page
     * @param scheduleRecordDTO
     * @return
     */
    IPage<SysUserScheduleDTO> getStaffOnDuty(Page<SysUserScheduleDTO> page, ScheduleRecordDTO scheduleRecordDTO);
    /**
     * 获取大屏的班组信息
     *
     * @param lineCode 线路code
     * @return
     */
    ScheduleBigScreenDTO getTeamData(String lineCode);
    /**
     * 获取大屏的班组信息-点击今日当班人数
     *
     * @param lineCode 线路code
     * @return
     */
    IPage<SysUserTeamDTO> getTodayOndutyDetail(String lineCode,  String orgcode,Page<SysUserTeamDTO> page);
    /**
     * 获取大屏的班组信息-点击总人员数
     *
     * @param lineCode 线路code
     * @return
     */
    IPage<SysUserTeamDTO> getTotalPepoleDetail(String lineCode,  String orgcode,Page<SysUserTeamDTO> page);

    List<SysDepartModel> getTeamBylineAndMajors(String lineCode);

}
