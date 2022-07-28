package com.aiurt.modules.sparepart.service.impl;

import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.aiurt.modules.sparepart.entity.SparePartMalfunction;
import com.aiurt.modules.sparepart.mapper.SparePartMalfunctionMapper;
import com.aiurt.modules.sparepart.service.ISparePartMalfunctionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: spare_part_malfunction
 * @Author: aiurt
 * @Date:   2022-07-27
 * @Version: V1.0
 */
@Service
public class SparePartMalfunctionServiceImpl extends ServiceImpl<SparePartMalfunctionMapper, SparePartMalfunction> implements ISparePartMalfunctionService {
    @Autowired
    private SparePartMalfunctionMapper sparePartMalfunctionMapper;
    /**
     * 查询列表
     * @param
     * @param sparePartMalfunction
     * @return
     */
    @Override
    public List<SparePartMalfunction> selectList(SparePartMalfunction sparePartMalfunction){
        return sparePartMalfunctionMapper.readAll(sparePartMalfunction);
    }
}
