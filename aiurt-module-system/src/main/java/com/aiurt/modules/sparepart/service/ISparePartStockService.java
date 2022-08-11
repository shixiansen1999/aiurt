package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
public interface ISparePartStockService extends IService<SparePartStock> {
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    List<SparePartStock> selectList(Page page, SparePartStock sparePartStock);
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    List<SparePartStock> selectLendList(Page page, SparePartStock sparePartStock);
}
