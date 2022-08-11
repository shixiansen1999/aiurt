package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_return_order
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
public interface ISparePartReturnOrderService extends IService<SparePartReturnOrder> {
    /**
     * 查询列表
     * @param page
     * @param sparePartReturnOrder
     * @return
     */
    List<SparePartReturnOrder> selectList(Page page, SparePartReturnOrder sparePartReturnOrder);
    /**
     * 编辑
     *
     * @param sparePartReturnOrder
     * @return
     */
    Result<?> update(SparePartReturnOrder sparePartReturnOrder);
    void updateOrder(SparePartOutOrder sparePartOutOrder);
}
