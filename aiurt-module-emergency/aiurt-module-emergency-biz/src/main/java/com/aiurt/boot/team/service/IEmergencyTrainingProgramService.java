package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTrainingProgramDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingProgram;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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

    /**
     * 添加
     * @param emergencyTrainingProgram
     * @return
     */
    Result<String> add(EmergencyTrainingProgram emergencyTrainingProgram);

    /**
     * 自动生成计划编号
     * @param
     * @return
     */
    String getTrainPlanCode();

    /**
     * 编辑
     * @param emergencyTrainingProgram
     * @return
     */
    Result<String> edit(EmergencyTrainingProgram emergencyTrainingProgram);
    /**
     * 删除
     * @param program
     * @return
     */
    void delete(EmergencyTrainingProgram program);

    /**
     * 发布
     * @param program
     * @return
     */
    void publish(EmergencyTrainingProgram program );
    /**
     * 根据id查询详情
     * @param emergencyTrainingProgram
     * @return
     */
    Result<EmergencyTrainingProgram> queryById(EmergencyTrainingProgram emergencyTrainingProgram);
}
