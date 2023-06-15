package com.aiurt.boot.api;

import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import org.jeecg.common.system.vo.LoginUser;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zwl
 */
public interface OverhaulApi {

    /**
     * 人员维度检修统计
     * @param startDate
     * @param endDate
     * @param teamId
     * @param userId
     * @return
     */
    Map<String, PersonnelTeamDTO> personnelInformation(Date startDate, Date endDate, List<String> teamId,String userId,List<String> userIds);


    /**
     * 班组维度检修统计
     * @param startDate
     * @param endDate
     * @param teamId
     * @return
     */
    Map<String, PersonnelTeamDTO> teamInformation(Date startDate, Date endDate, List<String> teamId);

    /**
     * 此方法是2023-06通信6期加的，为了修改时长
     * 获取班组在指定的时间内的总检修时长
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param teamIdList 班组的id列表
     * @return 返回一个Map，key是班组的id，value是总检修时长
     */
    Map<String, Integer> getTeamInspecitonTotalTime(Date startTime, Date endTime, List<String> teamIdList);

    /**
     * 此方法是2023-06通信6期加的，为了修改时长
     * 获取人员在指定的时间内的总检修时长
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userList 班组的id列表
     * @return 返回一个Map，key是人员的id，value是总检修时长
     */
    Map<String, Integer> getUserInspecitonTotalTime(Date startTime, Date endTime, List<LoginUser> userList);
}
