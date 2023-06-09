package com.aiurt.modules.training.service;

import com.aiurt.modules.training.entity.TrainingPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ITrainingPlanService extends IService<TrainingPlan> {

    /**
     * 培训计划-添加
     * @param trainingPlan 添加参数
     * @return 状态
     */
    Result<TrainingPlan> add(TrainingPlan trainingPlan);

    /**
     * 培训计划-编辑
     * @param trainingPlan 编辑参数
     * @return 状态
     */
    Result<TrainingPlan> edit(TrainingPlan trainingPlan);
}
