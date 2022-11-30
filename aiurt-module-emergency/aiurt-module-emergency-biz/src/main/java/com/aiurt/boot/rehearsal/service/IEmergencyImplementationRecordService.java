package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.dto.EmergencyRecordDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalRegisterDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyImplementationRecord;
import com.aiurt.boot.rehearsal.vo.EmergencyImplementationRecordVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_implementation_record
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyImplementationRecordService extends IService<EmergencyImplementationRecord> {
    /**
     * 应急实施记录-演练登记
     *
     * @param emergencyRehearsalRegisterDTO
     */
    String rehearsalRegister(EmergencyRehearsalRegisterDTO emergencyRehearsalRegisterDTO);

    /**
     * 应急实施记录-分页列表查询
     *
     * @param page
     * @param emergencyRecordDTO
     * @return
     */
    IPage<EmergencyImplementationRecordVO> queryPageList(Page<EmergencyImplementationRecordVO> page, EmergencyRecordDTO emergencyRecordDTO);
}
