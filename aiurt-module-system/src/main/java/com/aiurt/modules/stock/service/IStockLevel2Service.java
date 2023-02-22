package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockLevel2;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockLevel2Service extends IService<StockLevel2> {
    /**
     * 获取分页列表
     * @param page
     * @param stockLevel2
     * @return
     */
    IPage<StockLevel2> pageList(Page<StockLevel2> page, StockLevel2 stockLevel2);

    /**
     * 获取详情
     * @param id
     * @return
     */
    StockLevel2 getDetailById(String id);

    /**
     * 获取导出列表
     * @param ids
     * @return
     */
    List<StockLevel2> exportXls(StockLevel2 stockLevel2,String ids);
}
