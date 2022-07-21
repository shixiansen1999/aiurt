package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockLevel2;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockLevel2Service extends IService<StockLevel2> {
    IPage<StockLevel2> pageList(Page<StockLevel2> page, StockLevel2 stockLevel2);
}
