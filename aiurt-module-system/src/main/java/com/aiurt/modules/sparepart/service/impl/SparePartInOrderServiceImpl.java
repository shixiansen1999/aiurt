package com.aiurt.modules.sparepart.service.impl;


import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartInOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: spare_part_in_order
 * @Author: aiurt
 * @Date:   2022-07-22
 * @Version: V1.0
 */
@Service
public class SparePartInOrderServiceImpl extends ServiceImpl<SparePartInOrderMapper, SparePartInOrder> implements ISparePartInOrderService {
    @Autowired
    private SparePartInOrderMapper sparePartInOrderMapper;

    /**
     * 查询列表
     * @param page
     * @param sparePartInOrder
     * @return
     */
    @Override
    public List<SparePartInOrder> selectList(Page page, SparePartInOrder sparePartInOrder){
         return sparePartInOrderMapper.readAll(page,sparePartInOrder);
    }
}
