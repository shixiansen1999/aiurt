package com.aiurt.boot.team.mapper;

import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyTrainingProgramMapper extends BaseMapper<EmergencyTrainingProgram> {

    String getTrainingTeam(String id);
}
