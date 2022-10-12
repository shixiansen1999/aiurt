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
    /**
     * 获取班组集合
     *
     * @param ids 部门id集合字符串
     * @return  List<GroupModel>
     */
    List<GroupModel> queryGroupPageList(@Param("ids") List<String> ids, Page<GroupModel> page);

    /**
     * 获取人员集合
     *
     * @param ids 部门id集合字符串
     * @return  List<PersonnelModel>
     */
    List<PersonnelModel> queryUserPageList(@Param("ids")List<String> ids, Page<PersonnelModel> page);

    /**
     * 获取当前人员参与的已完成的任务集合
     *
     * @param userId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<TrainTaskDTO>
     */
    List<TrainTaskDTO> userTrainFinishedNum(@Param("userId")String userId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    /**
     * 获取当前人员在这个培训任务中是否完成考试，如果考完一次正式考试则返回1
     *
     * @param userId 用户id
     * @param taskId 任务id
     * @return Integer
     */
    Integer isExam(@Param("taskId")String taskId,@Param("userId")String userId);

    /**
     * 获取当前组织结构有多少次完成的培训任务
     *
     * @param teamId 组织结构id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Integer
     */
    Integer groupTrainFinishedNum(@Param("teamId")String teamId, @Param("startTime")String startTime, @Param("endTime")String endTime);

    /**
     * 获取用户的用户信息
     *
     * @param userId 用户id
     * @return TeamUserModel
     */
    TeamUserModel getUser(@Param("userId") String userId);

    /**
     * 获取班组的维修任务的信息
     *
     * @param userList 用户集合
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<FaultRepairRecordDTO>
     */
    List<FaultRepairRecordDTO> getRepairDuration(@Param("userList") List<String>  userList, @Param("startTime") DateTime startTime, @Param("endTime")DateTime endTime);

    /**
     * 获取组织结构的信息
     *
     * @param departId 组织结构id
     * @return TeamPortraitModel
     */
    TeamPortraitModel getDepart(@Param("departId") String departId);

    /**
     * 获取工区信息
     *
     * @param departId 组织结构id
     * @return List<TeamPortraitModel>
     */
    List<TeamPortraitModel> getWorkArea(String departId);

    /**
     * 获取工区管辖范围
     *
     * @param workAreaCode 工区code
     * @return List<TeamWorkAreaDTO>
     */
    List<TeamWorkAreaDTO> getStationDetails(@Param("workAreaCode")String workAreaCode);
}
