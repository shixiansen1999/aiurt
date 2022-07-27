package com.aiurt.modules.stock.service;

import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
 * @Version: V1.0
 */
public interface IStockOutboundMaterialsService extends IService<StockOutboundMaterials> {
    /**
     * 手动翻译
     * @param stockOutboundMaterials
     * @return
     */
    StockOutboundMaterials translate(StockOutboundMaterials stockOutboundMaterials);
}
