package com.aiurt.modules.sparepart.service;

import com.aiurt.modules.sparepart.entity.SparePartStock;
import com.aiurt.modules.sparepart.entity.dto.SparePartStockDTO;
import com.aiurt.modules.sparepart.entity.vo.SpareMaterialVO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartStockService extends IService<SparePartStock> {

    IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page, SparePartStockDTO sparePartStockDTO);

    List<SpareMaterialVO> queryMaterialByWarehouse(String warehouseCode);
}
