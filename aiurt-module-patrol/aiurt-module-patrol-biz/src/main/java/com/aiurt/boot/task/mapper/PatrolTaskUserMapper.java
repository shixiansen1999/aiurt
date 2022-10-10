package com.aiurt.boot.task.mapper;

import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.screen.model.ScreenDurationTask;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task_user
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskUserMapper extends BaseMapper<PatrolTaskUser> {

    String getUsername(@Param("userId") String userId);

    /**
     * 根据用户ID列表获取部门名称
     *
     * @param list
     * @return
     */
    List<String> getDeptName(@Param("list") List<String> list);

    /**
     * 获取当前用户
     *
     * @param id
     * @return
     */
    List<PatrolTask> getUserTask(String id);

    /**
     * 获取人员在指定时间范围内的任务时长(单位秒)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<ScreenDurationTask> getScreenUserDuration(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 获取同行人在指定时间范围内的任务时长(单位秒)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<ScreenDurationTask> getScreentAccompanyDuration(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     *  统计指派巡检计划数、实际完成数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getUserPlanNumber(@Param("useIds")List<String> useIds,@Param("startDate") String startDate, @Param("endDate")String endDate);

    /**
     * 统计同行人巡检计划数、实际完成数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getPeoplePlanNumber(@Param("useIds")List<String> useIds,@Param("startDate") String startDate, @Param("endDate")String endDate);
    /**
     *  统计指派巡检漏检数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getUserOmitNumber(@Param("useIds")List<String> useIds,@Param("startDate") String startDate, @Param("endDate")String endDate);

    /**
     * 统计同行人巡检漏检数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getPeopleOmitNumber(@Param("useIds")List<String> useIds,@Param("startDate") String startDate, @Param("endDate")String endDate);
}
