package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockLevel2Check;
import com.aiurt.modules.stock.entity.StockLevel2CheckDetail;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockLevel2CheckDetailService extends IService<StockLevel2CheckDetail> {
    /**
     * 分页
     * @param page
     * @param stockLevel2CheckDetail
     * @return
     */
    IPage<StockLevel2CheckDetail> pageList(Page<StockLevel2CheckDetail> page, StockLevel2CheckDetail stockLevel2CheckDetail);
}
