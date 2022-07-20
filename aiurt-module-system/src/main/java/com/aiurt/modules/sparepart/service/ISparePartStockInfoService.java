package com.aiurt.modules.sparepart.service;


import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;


/**
 * @Description: spare_part_stock_info
 * @Author: aiurt
 * @Date:   2022-07-20
 * @Version: V1.0
 */
public interface ISparePartStockInfoService extends IService<SparePartStockInfo> {
    /**
     * 添加
     *
     * @param sparePartStockInfo
     * @return
     */
    Result<?> add(SparePartStockInfo sparePartStockInfo);
    /**
     * 编辑
     *
     * @param sparePartStockInfo
     * @return
     */
    Result<?> update(SparePartStockInfo sparePartStockInfo);
}
