package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.dto.StockLevel2RespDTO;
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
    IPage<StockLevel2RespDTO> pageList(Page<StockLevel2> page, StockLevel2 stockLevel2);

    /**
     * 获取详情
     * @param id
     * @return
     */
    StockLevel2 getDetailById(String id);

    /**
     * 根据库存信息表的id获取详情
     * 这个方法其实是上面getDetailById方法的一些拓展：添加一些字段，返回对象是DTO等
     *
     * @param id 库存信息表id
     * @return StockLevel2RespDTO对象
     */
    StockLevel2RespDTO queryDetailById(String id);

    /**
     * 获取导出列表
     * @param ids
     * @return
     */
    List<StockLevel2> exportXls(StockLevel2 stockLevel2,String ids);
}
