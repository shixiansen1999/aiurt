package com.aiurt.boot.team.service;

import com.aiurt.boot.team.dto.EmergencyTrainingRecordDTO;
import com.aiurt.boot.team.entity.EmergencyTrainingRecord;
import com.aiurt.boot.team.vo.EmergencyTrainingRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_training_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyTrainingRecordService extends IService<EmergencyTrainingRecord> {

    /**
     * 应急队伍训练记录列表查询
     * @param emergencyTrainingRecordDTO
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage<EmergencyTrainingRecordVO> queryPageList(EmergencyTrainingRecordDTO emergencyTrainingRecordDTO, Integer pageNo, Integer pageSize);
}
