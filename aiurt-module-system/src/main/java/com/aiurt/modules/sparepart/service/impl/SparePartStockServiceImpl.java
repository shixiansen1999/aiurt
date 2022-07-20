package com.aiurt.modules.sparepart.service.impl;


import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStockDTO;
import com.aiurt.modules.sparepart.entity.vo.SpareMaterialVO;
import com.aiurt.modules.sparepart.mapper.SparePartStockMapper;
import com.aiurt.modules.sparepart.service.ISparePartStockService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Service
public class SparePartStockServiceImpl extends ServiceImpl<SparePartStockMapper, SparePartStock> implements ISparePartStockService {

    @Resource
    private SparePartStockMapper sparePartStockMapper;

    @Override
    public IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page, SparePartStockDTO sparePartStockDTO) {
        IPage<SparePartStockDTO> sparePartStockDTOIPage = sparePartStockMapper.queryPageList(page, sparePartStockDTO);
        return sparePartStockDTOIPage;
    }

    @Override
    public List<SpareMaterialVO> queryMaterialByWarehouse(String warehouseCode) {

        return sparePartStockMapper.queryMaterialByWarehouse(warehouseCode);
    }
}
