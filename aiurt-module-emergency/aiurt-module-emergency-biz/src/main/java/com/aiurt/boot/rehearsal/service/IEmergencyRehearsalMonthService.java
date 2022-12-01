package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyRehearsalMonthService extends IService<EmergencyRehearsalMonth> {

    String addMonthPlan(EmergencyRehearsalMonth emergencyRehearsalMonth);

    /**
     * 生成月计划编码
     *
     * @return
     */
    String getMonthCode();
}
