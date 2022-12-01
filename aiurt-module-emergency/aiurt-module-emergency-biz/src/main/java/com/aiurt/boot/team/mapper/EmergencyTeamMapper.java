package com.aiurt.boot.team.mapper;

import com.aiurt.boot.team.dto.EmergencyTeamDTO;
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

    List<EmergencyTeamDTO> getTrainingRecord(String id);
}
