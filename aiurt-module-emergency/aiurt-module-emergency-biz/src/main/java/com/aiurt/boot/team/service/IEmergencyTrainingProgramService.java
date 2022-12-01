package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_training_program
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyTrainingProgramService extends IService<EmergencyTrainingProgram> {

    /**
     * 查询列表
     * @param emergencyTrainingProgramDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<EmergencyTrainingProgram> queryPageList(EmergencyTrainingProgramDTO emergencyTrainingProgramDTO, Integer pageNo, Integer pageSize);
}
