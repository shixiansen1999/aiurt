package com.aiurt.boot.strategy.service;

import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IInspectionStrategyService extends IService<InspectionStrategy> {

    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualPlan(String id);
    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualNewPlan(String id);
}
