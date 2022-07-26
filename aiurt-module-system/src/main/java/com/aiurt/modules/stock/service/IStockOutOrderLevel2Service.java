package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.entity.StockOutOrderLevel2;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;

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
     * 导出
     * @param ids
     * @param request
     * @param response
     */
    void eqExport(String ids, HttpServletRequest request, HttpServletResponse response);
}
