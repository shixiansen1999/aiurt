package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.dto.EmergencyPlanRecordDTO;
import com.aiurt.boot.plan.entity.EmergencyPlan;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: emergency_plan
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyPlanService extends IService<EmergencyPlan> {
    /**
     * 应急预案列表查询
     * @param page
     * @param emergencyPlanDto
     * @return
     */
    IPage<EmergencyPlan> queryPageList(Page<EmergencyPlan> page, EmergencyPlanDTO emergencyPlanDto);

    /**
     * 保存并添加
     * @param emergencyPlanDto
     * @return
     */
    String saveAndAdd(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 编辑
     * @param emergencyPlanDto
     * @return
     */
    String edit(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 变更
     * @param emergencyPlanDto
     * @return
     */
    String change(EmergencyPlanDTO emergencyPlanDto);

    /**
     * 删除
     * @param id
     * @return
     */
    void delete(String id);

    /**
     * 应急预案台账提交
     * @param id
     * @return
     */
    String startProcess(String id);

    /**
     * 应急预案台账启用和停用
     * @param id
     * @return
     */
    String openOrStop(String id);


    /**
     * 应急预案台账通过id查询
     * @param id
     * @return
     */
    EmergencyPlanDTO queryById(String id);

}
