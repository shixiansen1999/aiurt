package com.aiurt.modules.personnelgroupstatistics.mapper;

import com.aiurt.modules.personnelgroupstatistics.model.PersonnelGroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.TrainTaskDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lkj
 */
@Component
public interface PersonnelGroupStatisticsMapper {

    List<PersonnelGroupModel> queryGroupPageList(@Param("ids") List<String> ids, Page<PersonnelGroupModel> page);

    List<PersonnelGroupModel> queryUserPageList(@Param("ids")List<String> ids, Page<PersonnelGroupModel> page);

    List<TrainTaskDTO> userTrainFinishedNum(@Param("userId")String userId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    Integer isExam(@Param("taskId")String taskId,@Param("userId")String userId);

    Integer groupTrainFinishedNum(@Param("teamId")String teamId, @Param("startTime")String startTime, @Param("endTime")String endTime);
}
