package com.aiurt.boot.personnelteam.mapper;

import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zwl
 */
@Component
public interface PersonnelTeamMapper {

    /**
     * 查询某一个时间节点的检修人对应的检修任务有多少
     * @param userIdList
     * @param status
     * @param startDate
     * @param endDate
     * @param userId
     * @return
     */
    List<PersonnelTeamDTO> getScheduledTask(@Param("userIdList") List<String> userIdList,
                                            @Param("status") Long status,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate,
                                            @Param("userId") String userId);

    /**
     * 查询某一个时间节点的检修人对应的检修任务的总工时
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    PersonnelTeamDTO getUserTime(@Param("userId") String userId,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);


    /**
     * 查询某一个时间节点的同行人对应的检修任务的总工时
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    PersonnelTeamDTO getUserPeerTime(@Param("userId") String userId,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);

    /**
     * 查询某一个时间节点的班组对应的检修任务有多少
     * @param teamCodeList
     * @param status
     * @param startDate
     * @param endDate
     * @return
     */
    List<PersonnelTeamDTO> getTeamTask(@Param("teamCodeList") List<String> teamCodeList,
                                       @Param("status") Long status,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);


    /**
     * 查询某一个时间节点的班组对应的检修任务的总工时
     * @param userIdList
     * @param startDate
     * @param endDate
     * @return
     */
    List<PersonnelTeamDTO> getTeamTime(@Param("userIdList") List<String> userIdList,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);


    /**
     * 查询某一个时间节点的班组对应的检修任务的总工时
     * @param userIdList
     * @param startDate
     * @param endDate
     * @return
     */
    List<PersonnelTeamDTO> getTeamPeerTime(@Param("userIdList") List<String> userIdList,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);


    /**
     * 根据班组id集合查询班组编码集合
     * @param idList
     * @return
     */
    List<String> getIdList(@Param("idList") List<String> idList);


    /**
     * 根据班组id查询班组编码
     * @param code
     * @return
     */
    String getId(@Param("code") String code);
}