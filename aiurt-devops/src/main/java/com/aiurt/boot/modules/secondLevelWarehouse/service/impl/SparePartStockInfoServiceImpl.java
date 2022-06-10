package com.aiurt.boot.modules.secondLevelWarehouse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStockVO;
import com.aiurt.boot.modules.secondLevelWarehouse.mapper.SparePartStockInfoMapper;
import com.aiurt.boot.modules.secondLevelWarehouse.service.ISparePartStockInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class SparePartStockInfoServiceImpl extends ServiceImpl<SparePartStockInfoMapper, SparePartStockVO> implements ISparePartStockInfoService {

    @Resource
    private SparePartStockInfoMapper sparePartStockInfoMapper;
    @Override
    public List<SparePartStockVO> queryList() {
        return  sparePartStockInfoMapper.queryList();
    }
}
