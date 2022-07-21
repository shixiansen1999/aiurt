package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockInOrderLevel2Service extends IService<StockInOrderLevel2> {
    /**
     * 新增获取入库编号
     * @return
     */
    StockInOrderLevel2 getSubmitPlanCode();

    /**
     * 二级库入库管理-添加
     * @param stockInOrderLevel2
     */
    void add(StockInOrderLevel2 stockInOrderLevel2);

    /**
     * 二级库入库管理-编辑
     * @param stockInOrderLevel2
     * @return
     */
    boolean edit(StockInOrderLevel2 stockInOrderLevel2);

    /**
     * 提交
     * @param status
     * @param code
     * @return
     */
    boolean submitPlan(Integer status,String code);
}
