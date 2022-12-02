package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.entity.EmergencyPlanRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_plan_record
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanRecordService extends IService<EmergencyPlanRecord> {

    /**
     * 应急预案启动记录新增
     * @param emergencyPlanRecordDto
     * @return
     */
    String saveAndAdd(EmergencyPlanRecordDTO emergencyPlanRecordDto);

    /**
     * 应急预案启动记录编辑
     * @param emergencyPlanRecordDto
     * @return
     */
    String edit(EmergencyPlanRecordDTO emergencyPlanRecordDto);

    /**
     * 应急预案启动记录删除
     * @param id
     */
    void delete(String id);
}
