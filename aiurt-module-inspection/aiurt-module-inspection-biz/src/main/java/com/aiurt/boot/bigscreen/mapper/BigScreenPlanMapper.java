package com.aiurt.boot.bigscreen.mapper;

import com.aiurt.boot.index.dto.TeamPortraitDTO;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
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

    List<TeamPortraitDTO> getWorkAreaById(@Param("teamCode") String teamCode);

    List<String> getOnDuty(@Param("today")String today,@Param("userList") List<LoginUser> userList);

    List<RepairRecordDetailDTO> getRepairDuration(@Param("userList") List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    Long getFaultTotalTime(@Param("userList")List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);

    Long getFaultTotalTimeByPeer(@Param("userList")List<LoginUser> userList,@Param("beginDate")Date beginDate,@Param("endDate") Date endDate);
}