package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
public interface ISparePartOutOrderService extends IService<SparePartOutOrder> {
    /**
     * 查询列表
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    List<SparePartOutOrder> selectList(Page page, SparePartOutOrder sparePartOutOrder);

    /**
     * 查询列表不分页
     * @param sparePartOutOrder
     * @return
     */
    List<SparePartOutOrder> selectListById(SparePartOutOrder sparePartOutOrder);
    /**
     * 查询已出库的物资编号
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    List<SparePartOutOrder> selectMaterial(Page page, SparePartOutOrder sparePartOutOrder);
    /**
     * 编辑
     *
     * @param sparePartOutOrder
     * @return
     */
    Result<?> update( SparePartOutOrder sparePartOutOrder);

    /**
     * 查询本班组的信息出库的物资
     * @param materialCode 物资编码
     * @return
     */
    List<SparePartOutOrder> querySparePartOutOrder(String materialCode);

    /***
     * app-出库
     * @param sparePartStock
     */
    void appOutbound(SparePartStock sparePartStock);

    /**
     * 三级库出库管理-根据出库单号查询详情
     * @param orderCode
     * @return
     */
    SparePartOutOrder queryByOrderCode(String orderCode);
}
