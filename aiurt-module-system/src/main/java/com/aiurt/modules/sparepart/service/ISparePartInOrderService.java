package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
public interface ISparePartInOrderService extends IService<SparePartInOrder> {
    /**
     * 查询列表
     * @param page
     * @param sparePartInOrder
     * @return
     */
    List<SparePartInOrder> selectList(Page page, SparePartInOrder sparePartInOrder);
    /**
     * 编辑
     *
     * @param sparePartInOrder
     * @return
     */
    Result<?> update(SparePartInOrder sparePartInOrder);
    /**
     * 编辑
     *
     * @param sparePartInOrder
     * @return
     */
    Result<?> batchStorage(List<SparePartInOrder>  sparePartInOrder);
}
