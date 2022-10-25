package com.aiurt.modules.stock.service;

import com.aiurt.modules.sparepart.entity.SparePartApply;
import com.aiurt.modules.sparepart.entity.SparePartStockInfo;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * @Description:
 * @Author: swsc
 * @Date: 2021-09-15
/**
 * @Description: stock_out_order_level2
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
public interface IStockOutOrderLevel2Service extends IService<StockOutOrderLevel2> {

    /**
     * 分页查询
     * @param page
     * @param stockOutOrderLevel2
     * @return
     */
    IPage<StockOutOrderLevel2> pageList(Page<StockOutOrderLevel2> page, StockOutOrderLevel2 stockOutOrderLevel2);

    /**
     * 权限过滤后，列表数据整体
     * @return
     */
    List<StockOutOrderLevel2> selectList();
    /**
     * 获取物资列表
     * @param id
     * @return
     */
    SparePartApply getList(String id);

    /**
     * 确认出库
     * @param sparePartApply
     * @param stockOutOrderLevel2
     * @throws ParseException
     */
    void confirmOutOrder(SparePartApply sparePartApply, StockOutOrderLevel2 stockOutOrderLevel2) throws ParseException;
}
