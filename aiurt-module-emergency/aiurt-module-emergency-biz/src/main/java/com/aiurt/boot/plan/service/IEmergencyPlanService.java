package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.EmergencyPlanDTO;
import com.aiurt.boot.plan.entity.EmergencyPlan;
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
     * 应急预案台账审核
     * @param id
     */
    void audit(String id);

    /**
     * 查看详情
     * @param id
     * @return
     */
    List<EmergencyPlanDTO> getPlanInfo(String id);

}
