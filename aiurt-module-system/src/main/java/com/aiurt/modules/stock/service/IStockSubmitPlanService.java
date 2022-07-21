package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockSubmitPlanService extends IService<StockSubmitPlan> {
    /**
     * 新增获取提报计划编号
     * @return
     */
    StockSubmitPlan getSubmitPlanCode();

    /**
     * 物资提报计划-添加
     * @param stockSubmitPlan
     */
    void add(StockSubmitPlan stockSubmitPlan);

    /**
     * 物资提报计划-编辑
     * @param stockSubmitPlan
     * @return
     */
    boolean edit(StockSubmitPlan stockSubmitPlan);
}
