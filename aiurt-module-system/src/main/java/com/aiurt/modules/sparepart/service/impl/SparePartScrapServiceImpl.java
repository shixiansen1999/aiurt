package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.sparepart.entity.SparePartReturnOrder;
import com.aiurt.modules.sparepart.entity.SparePartScrap;
import com.aiurt.modules.sparepart.mapper.SparePartScrapMapper;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartScrapService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: spare_part_scrap
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Service
public class SparePartScrapServiceImpl extends ServiceImpl<SparePartScrapMapper, SparePartScrap> implements ISparePartScrapService {
    @Autowired
    private SparePartScrapMapper sparePartScrapMapper;
    /**
     * 查询列表
     * @param page
     * @param sparePartScrap
     * @return
     */
    @Override
    public List<SparePartScrap> selectList(Page page, SparePartScrap sparePartScrap){
        return sparePartScrapMapper.readAll(page,sparePartScrap);
    }
}
