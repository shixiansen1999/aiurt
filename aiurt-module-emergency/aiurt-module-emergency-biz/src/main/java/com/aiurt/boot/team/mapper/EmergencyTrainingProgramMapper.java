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

    /**
     * 获取队伍名称
     * @param id
     * @return
     */
    String getTrainingTeam(String id);
    /**
     * 获取负责人名称
     * @param id
     * @return
     */
    String getTrainees(String id);
}
