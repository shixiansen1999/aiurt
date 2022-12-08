package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.entity.EmergencyPlanRecordDisposalProcedure;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: emergency_plan_record_disposal_procedure
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanRecordDisposalProcedureService extends IService<EmergencyPlanRecordDisposalProcedure> {
    /**
     * 根据应急预案id查询处置程序
     * @param id
     * @return
     */
    List<EmergencyPlanRecordDisposalProcedure> queryById(String id);
}
