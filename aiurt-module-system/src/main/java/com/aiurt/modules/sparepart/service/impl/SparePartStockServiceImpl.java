package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: spare_part_stock
 * @Author: aiurt
 * @Date:   2022-07-25
 * @Version: V1.0
 */
@Service
public class SparePartStockServiceImpl extends ServiceImpl<SparePartStockMapper, SparePartStock> implements ISparePartStockService {
    @Autowired
    private SparePartStockMapper sparePartStockMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartStock
     * @return
     */
    @Override
    public List<SparePartStock> selectList(Page page, SparePartStock sparePartStock){
        return sparePartStockMapper.readAll(page,sparePartStock);
    }
}
