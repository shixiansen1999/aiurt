package com.aiurt.modules.personnelgroupstatistics.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.index.dto.TeamWorkAreaDTO;
import com.aiurt.modules.personnelgroupstatistics.dto.FaultRepairRecordDTO;
import com.aiurt.modules.personnelgroupstatistics.dto.TrainTaskDTO;
import com.aiurt.modules.personnelgroupstatistics.model.GroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.PersonnelModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lkj
 */
@Component
public interface PersonnelGroupStatisticsMapper {

    List<GroupModel> queryGroupPageList(@Param("ids") List<String> ids, Page<GroupModel> page);

    List<PersonnelModel> queryUserPageList(@Param("ids")List<String> ids, Page<PersonnelModel> page);

    List<TrainTaskDTO> userTrainFinishedNum(@Param("userId")String userId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    Integer isExam(@Param("taskId")String taskId,@Param("userId")String userId);

    Integer groupTrainFinishedNum(@Param("teamId")String teamId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    TeamUserModel getUser(@Param("userId") String userId);

    List<FaultRepairRecordDTO> getRepairDuration(@Param("userList") List<String>  userList, @Param("startTime") DateTime startTime, @Param("endTime")DateTime endTime);

    TeamPortraitModel getDepart(@Param("departId") String departId);

    List<TeamPortraitModel> getWorkArea(String departId);

    List<TeamWorkAreaDTO> getStationDetails(@Param("workAreaCode")String workAreaCode);
}
