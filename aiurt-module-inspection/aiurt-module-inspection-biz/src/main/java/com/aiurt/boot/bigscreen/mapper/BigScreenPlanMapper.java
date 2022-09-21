package com.aiurt.boot.bigscreen.mapper;

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

    List<TeamPortraitDTO> getAllSysDepart();

    List<TeamPortraitDTO> getWorkAreaByCode(@Param("teamCode") String teamCode);

    List<String> getOnDuty(@Param("today")String today,@Param("userList") List<LoginUser> userList);

    List<RepairRecordDetailDTO> getRepairDuration(@Param("userList") List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    Long getInspecitonTotalTime(@Param("userList")List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    Long getInspecitonTotalTimeByPeer(@Param("userList")List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    List<TeamPortraitDTO> getWorkAreaById(@Param("teamId")String teamId);

    List<TeamWorkAreaDTO> getStationDetails(@Param("workAreaCode")String workAreaCode);

    List<TeamUserDTO> getUserList(Page<TeamUserDTO> page, String teamId);

    List<TeamUserDTO> getReconditionTime(@Param("userList")List<TeamUserDTO> userList, @Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    List<TeamUserDTO> getReconditionTimeByPeer(@Param("userList")List<TeamUserDTO> userList, @Param("beginDate")Date beginDate,@Param("endDate") Date endDate);
}