package com.aiurt.boot.bigscreen.mapper;

import com.aiurt.boot.index.dto.TaskUserDTO;
import com.aiurt.boot.index.dto.TeamPortraitDTO;
import com.aiurt.boot.index.dto.TeamUserDTO;
import com.aiurt.boot.index.dto.TeamWorkAreaDTO;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author lkj
 */
@Component
public interface BigScreenPlanMapper {

    /**
     * 获取专业的部门
     * @param majorCodes
     * @return
     */
    List<TeamPortraitDTO> getAllSysDepart(@Param("majorCodes")List<String> majorCodes);

    /**
     * 获取工区信息
     * @param teamCode
     * @return
     */
    List<TeamPortraitDTO> getWorkAreaByCode(@Param("teamCode") String teamCode);

    /**
     * 获取排班人员
     * @param today
     * @param userList
     * @return
     */
    List<String> getOnDuty(@Param("today")String today,@Param("userList") List<LoginUser> userList);

    /**
     * 获取维修记录信息
     * @param userList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<RepairRecordDetailDTO> getRepairDuration(@Param("userList") List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    /**
     * 获取本班组指派人在指定时间范围内的所有任务时长
     * @param userList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TaskUserDTO> getInspecitonTotalTime(@Param("userList")List<LoginUser> userList, @Param("beginDate")Date beginDate, @Param("endDate") Date endDate);

    /**
     * 获取本班组同行人在指定时间范围内的所有任务时长
     * @param userList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TaskUserDTO> getInspecitonTotalTimeByPeer(@Param("userList")List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    /**
     * 获取工区信息
     * @param teamId
     * @return
     */
    List<TeamPortraitDTO> getWorkAreaById(@Param("teamId")String teamId);

    /**
     * 获取工区管辖范围
     * @param workAreaCode
     * @return
     */
    List<TeamWorkAreaDTO> getStationDetails(@Param("workAreaCode")String workAreaCode);

    /**
     * 班组的人员
     * @param page
     * @param teamId
     * @return
     */
    List<TeamUserDTO> getUserList(Page<TeamUserDTO> page, String teamId);

    /**
     * 查询检修任务工作时长
     * @param userList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TeamUserDTO> getReconditionTime(@Param("userList")List<TeamUserDTO> userList, @Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    /**
     * 查询检修任务同行人工作时长
     * @param userList
     * @param beginDate
     * @param endDate
     * @return
     */
    List<TeamUserDTO> getReconditionTimeByPeer(@Param("userList")List<TeamUserDTO> userList, @Param("beginDate")Date beginDate,@Param("endDate") Date endDate);
}