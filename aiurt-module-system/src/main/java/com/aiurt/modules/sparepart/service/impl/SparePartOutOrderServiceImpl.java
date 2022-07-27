package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartOutOrder;
import com.aiurt.modules.sparepart.mapper.SparePartInOrderMapper;
import com.aiurt.modules.sparepart.mapper.SparePartOutOrderMapper;
import com.aiurt.modules.sparepart.service.ISparePartOutOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: spare_part_out_order
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartOutOrderServiceImpl extends ServiceImpl<SparePartOutOrderMapper, SparePartOutOrder> implements ISparePartOutOrderService {
    @Autowired
    private SparePartOutOrderMapper sparePartOutOrderMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartOutOrder
     * @return
     */
    @Override
    public List<SparePartOutOrder> selectList(Page page, SparePartOutOrder sparePartOutOrder){
        return sparePartOutOrderMapper.readAll(page,sparePartOutOrder);
    }
}
