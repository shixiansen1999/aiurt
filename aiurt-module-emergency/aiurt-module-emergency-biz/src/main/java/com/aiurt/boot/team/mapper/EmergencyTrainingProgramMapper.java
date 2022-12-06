package com.aiurt.boot.team.mapper;

import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
    String getTrainingTeam(@Param("id")String id);
    /**
     * 获取负责人名称
     * @param id
     * @return
     */
    String getTrainees(@Param("id")String id);

    /**
     * 根据应急队伍选择训练计划
     * @param page
     * @param id
     * @param emergencyTrainingProgramDTO
     * @return
     */
    List<EmergencyTrainingProgram> getTrainingProgram(Page<EmergencyTrainingProgram> page, @Param("id")String id, @Param("emergencyTrainingProgramDTO") EmergencyTrainingProgramDTO emergencyTrainingProgramDTO);

}
