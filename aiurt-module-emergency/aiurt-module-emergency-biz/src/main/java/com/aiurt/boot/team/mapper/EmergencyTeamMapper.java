package com.aiurt.boot.team.mapper;

import com.aiurt.boot.team.dto.EmergencyTeamTrainingDTO;
import com.aiurt.boot.team.entity.EmergencyTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: emergency_team
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyTeamMapper extends BaseMapper<EmergencyTeam> {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    List<EmergencyTeamTrainingDTO> getTrainingRecord(String id);

    /**
     * 根据训练计划查找应急队伍
     * @param id
     * @return
     */
    List<EmergencyTeam>  getTeamByTrainingProgram(String id);
}
