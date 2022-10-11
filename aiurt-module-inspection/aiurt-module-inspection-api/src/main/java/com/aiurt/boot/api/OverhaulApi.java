package com.aiurt.boot.api;

import com.aiurt.boot.task.dto.PersonnelTeamDTO;

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
    Map<String, PersonnelTeamDTO> personnelInformation(Date startDate, Date endDate, List<String> teamId,String userId);


    /**
     * 班组维度检修统计
     * @param startDate
     * @param endDate
     * @param teamId
     * @return
     */
    Map<String, PersonnelTeamDTO> teamInformation(Date startDate, Date endDate, List<String> teamId);
}
